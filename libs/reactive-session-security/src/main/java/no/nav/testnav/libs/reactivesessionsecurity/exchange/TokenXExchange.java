package no.nav.testnav.libs.reactivesessionsecurity.exchange;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import no.nav.testnav.libs.reactivesessionsecurity.domain.AccessToken;
import no.nav.testnav.libs.reactivesessionsecurity.domain.ServerProperties;
import no.nav.testnav.libs.reactivesessionsecurity.domain.TokenX;
import no.nav.testnav.libs.reactivesessionsecurity.domain.tokenx.WellKnownConfig;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.TokenResolver;

@Slf4j
@Service
@Import({
        TokenX.class
})
public class TokenXExchange implements GenerateTokenExchange {
    private final TokenResolver tokenService;
    private final WebClient webClient;
    private final TokenX tokenX;

    public Mono<WellKnownConfig> fetchWellKnownConfig() {
        return webClient
                .get()
                .uri(tokenX.getWellKnownUrl())
                .retrieve()
                .bodyToMono(WellKnownConfig.class)
                .cache(
                        value -> Duration.ofDays(1),
                        value -> Duration.ZERO,
                        () -> Duration.ZERO
                ).doOnNext(value -> log.info("Well known config {}.", value));
    }


    TokenXExchange(TokenX tokenX, TokenResolver tokenService) {
        this.webClient = WebClient.builder().build();
        this.tokenX = tokenX;
        this.tokenService = tokenService;
    }

    private String toScope(ServerProperties serverProperties) {
        return serverProperties.getCluster() + ":" + serverProperties.getNamespace() + ":" + serverProperties.getName();
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

    @Override
    public Mono<AccessToken> generateToken(ServerProperties serverProperties, ServerWebExchange exchange) {
        var scope = toScope(serverProperties);
        log.info("Generer TokenX for {}...", scope);
        return tokenService
                .getToken(exchange)
                .flatMap(token -> fetchWellKnownConfig()
                        .flatMap(config ->
                                webClient
                                        .post()
                                        .uri(config.getTokenEndpoint())
                                        .body(BodyInserters
                                                .fromFormData("grant_type", config.getGrantTypesSupported().get(0))
                                                .with("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer")
                                                .with("client_assertion", createClientAssertion(config.getTokenEndpoint()))
                                                .with("subject_token_type", "urn:ietf:params:oauth:token-type:jwt")
                                                .with("subject_token", token.getValue())
                                                .with("audience", scope)
                                        ).retrieve()
                                        .bodyToMono(AccessToken.class)
                                        .doOnError(error -> {
                                            if (error instanceof WebClientResponseException) {
                                                log.error(
                                                        "Feil ved henting av access token. \n{}",
                                                        ((WebClientResponseException) error).getResponseBodyAsString()
                                                );
                                            } else {
                                                log.error("Feil ved henting av access token.", error);
                                            }
                                        })
                        )
                ).doOnNext(value -> log.info("Token generert for {}.", scope));
    }
}
