package no.nav.testnav.libs.securitycore.command.idporten;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.command.ExchangeCommand;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.Token;
import no.nav.testnav.libs.securitycore.domain.idporten.IdportenProperties;
import no.nav.testnav.libs.securitycore.domain.tokenx.WellKnown;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class OnBehalfOfExchangeCommand implements ExchangeCommand {

    private final WebClient webClient;
    private final IdportenProperties idportenProperties;
    private final String scope;
    private final Token token;

    @Override
    public Mono<AccessToken> call() {
        log.info("Access token opprettet for OAuth 2.0 On-Behalf-Of Flow. Scope: {}.", scope);
        return webClient
                .get()
                .uri(idportenProperties.getWellKnownUrl())
                .retrieve().bodyToMono(WellKnown.class)
                .flatMap(config ->
                        webClient
                                .post()
                                .uri(config.getToken_endpoint())
                                .body(BodyInserters
                                        .fromFormData("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange")
                                        .with("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer")
                                        .with("client_assertion", createClientAssertion(config.getToken_endpoint()))
                                        .with("subject_token_type", "urn:ietf:params:oauth:token-type:jwt")
                                        .with("subject_token", token.getAccessTokenValue())
                                        .with("audience", scope)
                                ).retrieve()
                                .bodyToMono(AccessToken.class)
                                .doOnError(
                                        WebClientResponseException.class::isInstance,
                                        throwable -> log.error(
                                                "Feil ved henting av access token. Feilmelding: {}.",
                                                ((WebClientResponseException) throwable).getResponseBodyAsString()
                                        )
                                )
                                .doOnError(
                                        throwable -> !(throwable instanceof WebClientResponseException),
                                        throwable -> log.error("Feil ved henting av access token.", throwable)
                                )
                );
    }

    @SneakyThrows
    private String createClientAssertion(String tokenEndpoint) {
        var date = Calendar.getInstance();
        var jwk = RSAKey.parse(idportenProperties.getJwk());
        return JWT.create()
                .withSubject(idportenProperties.getClientId())
                .withIssuer(idportenProperties.getClientId())
                .withAudience(tokenEndpoint)
                .withJWTId(UUID.randomUUID().toString())
                .withIssuedAt(date.getTime())
                .withNotBefore(date.getTime())
                .withExpiresAt(new Date(date.getTimeInMillis() + (120 * 1000)))
                .withKeyId(jwk.getKeyID())
                .sign(Algorithm.RSA256(null, jwk.toRSAKey().toRSAPrivateKey()));
    }
}
