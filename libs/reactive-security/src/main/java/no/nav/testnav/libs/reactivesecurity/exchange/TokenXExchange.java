package no.nav.testnav.libs.reactivesecurity.exchange;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedTokenAction;
import no.nav.testnav.libs.reactivesecurity.domain.AccessToken;
import no.nav.testnav.libs.reactivesecurity.domain.ServerProperties;
import no.nav.testnav.libs.reactivesecurity.domain.TokenX;
import no.nav.testnav.libs.reactivesecurity.domain.WellKnownConfig;

@Slf4j
@Service
public class TokenXExchange implements GenerateTokenExchange {
    private final GetAuthenticatedTokenAction getAuthenticatedTokenAction;
    private final WebClient webClient;
    private final TokenX tokenX;

    TokenXExchange(TokenX tokenX, GetAuthenticatedTokenAction tokenResolver) {
        this.webClient = WebClient.builder().build();
        this.tokenX = tokenX;
        this.getAuthenticatedTokenAction = tokenResolver;
    }


    private String toScope(ServerProperties serverProperties) {
        return serverProperties.getCluster() + ":" + serverProperties.getNamespace() + ":" + serverProperties.getName();
    }

    @Override
    public Mono<AccessToken> generateToken(ServerProperties serverProperties) {
        return getAuthenticatedTokenAction.call().flatMap(token -> webClient
                .get()
                .uri(tokenX.getWellKnownUrl())
                .retrieve()
                .bodyToMono(WellKnownConfig.class)
                .flatMap(config ->
                        webClient
                                .post()
                                .uri(config.getTokenEndpoint())
                                .body(BodyInserters
                                        .fromFormData("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange")
                                        .with("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer")
                                        .with("client_assertion", createClientAssertion(config.getTokenEndpoint()))
                                        .with("subject_token_type", "urn:ietf:params:oauth:token-type:jwt")
                                        .with("subject_token", token.getValue())
                                        .with("audience", toScope(serverProperties))
                                ).retrieve()
                                .bodyToMono(AccessToken.class)
                                .doOnError(error -> {
                                    if (error instanceof WebClientResponseException) {
                                        log.error(
                                                "Feil ved henting av access token. Feilmelding: {}.",
                                                ((WebClientResponseException) error).getResponseBodyAsString()
                                        );
                                    } else {
                                        log.error("Feil ved henting av access token.", error);
                                    }
                                })
                )
        );
    }

    @SneakyThrows
    private String createClientAssertion(String tokenEndpoint) {
        var date = Calendar.getInstance();
        var jwk = RSAKey.parse(tokenX.getJwk());
        return JWT.create()
                .withSubject(tokenX.getClientId())
                .withIssuer(tokenX.getClientId())
                .withAudience(tokenEndpoint)
                .withJWTId(UUID.randomUUID().toString())
                .withIssuedAt(date.getTime())
                .withNotBefore(date.getTime())
                .withExpiresAt(new Date(date.getTimeInMillis() + (120 * 1000)))
                .withKeyId(jwk.getKeyID())
                .sign(Algorithm.RSA256(null, jwk.toRSAKey().toRSAPrivateKey()));
    }
}
