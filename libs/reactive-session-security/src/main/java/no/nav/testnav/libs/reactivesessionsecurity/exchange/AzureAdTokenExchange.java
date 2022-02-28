package no.nav.testnav.libs.reactivesessionsecurity.exchange;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.TokenResolver;
import no.nav.testnav.libs.securitycore.command.azuread.ClientCredentialExchangeCommand;
import no.nav.testnav.libs.securitycore.command.azuread.OnBehalfOfExchangeCommand;
import no.nav.testnav.libs.securitycore.command.azuread.RefreshAccessTokenCommand;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitycore.domain.Token;
import no.nav.testnav.libs.securitycore.domain.azuread.AzureNavClientCredential;
import no.nav.testnav.libs.securitycore.domain.azuread.ClientCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Service
@Import({
        AzureNavClientCredential.class
})
public class AzureAdTokenExchange implements ExchangeToken {
    private final WebClient webClient;
    private final TokenResolver tokenResolver;
    private final ClientCredential clientCredential;

    public AzureAdTokenExchange(
            @Value("${AAD_ISSUER_URI}") String issuerUrl,
            TokenResolver tokenResolver,
            AzureNavClientCredential clientCredential
    ) {
        this.webClient = WebClient
                .builder()
                .baseUrl(issuerUrl + "/oauth2/v2.0/token")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        this.tokenResolver = tokenResolver;
        this.clientCredential = clientCredential;
    }

    @Override
    public Mono<AccessToken> exchange(ServerProperties serverProperties, ServerWebExchange exchange) {
        return tokenResolver
                .getToken(exchange)
                .flatMap(token -> {
                    if (token.getExpiredAt().isBefore(LocalDateTime.now().toInstant(ZoneOffset.UTC).plusSeconds(180))) {
                        return refreshAccessToken(serverProperties, token.getRefreshTokenValue()).flatMap(accessToken -> {
                            log.info("Accesstoken har utløpt! Prøver å hente nytt accesstoken fra refreshtoken.");
                            return new OnBehalfOfExchangeCommand(
                                    webClient,
                                    clientCredential,
                                    serverProperties.toAzureAdScope(),
                                    Token.builder()
                                            .userId(token.getUserId())
                                            .clientCredentials(token.isClientCredentials())
                                            .accessTokenValue(accessToken.getTokenValue())
                                            .refreshTokenValue(token.getRefreshTokenValue())
                                            .expiredAt(LocalDateTime.now().toInstant(ZoneOffset.UTC).plusSeconds(600))
                                            .build()
                            ).call();
                        });
                    } else {
                        return new OnBehalfOfExchangeCommand(
                                webClient,
                                clientCredential,
                                serverProperties.toAzureAdScope(),
                                token
                        ).call();
                    }
                });
    }

    public Mono<AccessToken> generateClientCredentialAccessToken(ServerProperties serverProperties) {
        return new ClientCredentialExchangeCommand(webClient, clientCredential, serverProperties.toAzureAdScope()).call();
    }

    public Mono<AccessToken> refreshAccessToken(ServerProperties serverProperties, String refreshToken) {
        return new RefreshAccessTokenCommand(webClient, clientCredential, serverProperties.toAzureAdScope(), refreshToken).call();
    }

}
