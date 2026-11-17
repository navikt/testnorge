package no.nav.testnav.libs.securitycore.texas;

import no.nav.testnav.libs.securitycore.command.azuread.ClientCredentialExchangeCommand;
import no.nav.testnav.libs.securitycore.command.azuread.OnBehalfOfExchangeCommand;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.Token;
import no.nav.testnav.libs.securitycore.domain.azuread.AzureClientCredential;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtAudienceValidator;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class LocalTexasClient extends TexasClient {

    private final WebClient localauth;
    private final AzureClientCredential clientCredential;
    private final Map<String, ReactiveJwtDecoder> decoders = new HashMap<>();

    public LocalTexasClient(String tokenEndpoint, String exchangeEndpoint, String localSecret, AzureClientCredential clientCredential,
                            String entraIdIssuer, String tokenXIssuer, String tokenXClientId) {
        super(tokenEndpoint, exchangeEndpoint, null, localSecret);
        this.localauth = WebClient.builder().baseUrl(clientCredential.getTokenEndpoint()).build();
        this.clientCredential = clientCredential;
        decoders.put(ENTRA_ID, decoder(entraIdIssuer, clientCredential.getClientId()));
        if (nonNull(tokenXClientId)) {
            decoders.put(TOKENX, decoder(tokenXIssuer, tokenXClientId));
        }
    }

    private static ReactiveJwtDecoder decoder(String issuer, String audience) {
        var decoder = NimbusReactiveJwtDecoder.withIssuerLocation(issuer).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithValidators(new JwtIssuerValidator(issuer), new JwtAudienceValidator(audience)));
        return decoder;
    }

    @Override
    public Mono<AccessToken> token(String identityProvider, String target) {
        if (ENTRA_ID.equals(identityProvider)) {
            return new ClientCredentialExchangeCommand(localauth, clientCredential, target).call();
        }
        return super.token(identityProvider, target);
    }

    @Override
    public Mono<AccessToken> exchange(String identityProvider, String target, String userToken) {
        if (ENTRA_ID.equals(identityProvider)) {
            var token = Token.builder().accessTokenValue(userToken).build();
            return new OnBehalfOfExchangeCommand(localauth, clientCredential, target, token).call();
        }
        return super.exchange(identityProvider, target, userToken);
    }

    @Override
    public Mono<Map<String, Object>> introspect(String identityProvider, String token) {
        var decoder = decoders.get(identityProvider);
        if (isNull(decoder)) {
            return Mono.error(new BadJwtException("Ingen lokal validering for " + identityProvider));
        }
        return decoder.decode(token).map(Jwt::getClaims);
    }
}
