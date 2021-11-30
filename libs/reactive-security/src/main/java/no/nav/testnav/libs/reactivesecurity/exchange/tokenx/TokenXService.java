package no.nav.testnav.libs.reactivesecurity.exchange.tokenx;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.reactivesecurity.domain.AccessToken;
import no.nav.testnav.libs.reactivesecurity.domain.ResourceServerType;
import no.nav.testnav.libs.reactivesecurity.domain.TokenX;
import no.nav.testnav.libs.reactivesecurity.domain.tokenx.v1.WellKnown;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenService;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Slf4j
@Service
@ConditionalOnProperty("spring.security.oauth2.resourceserver.tokenx.issuer-uri")
public class TokenXService implements TokenService {
    private final GetAuthenticatedToken getAuthenticatedTokenAction;
    private final WebClient webClient;
    private final TokenX tokenX;

    TokenXService(TokenX tokenX, GetAuthenticatedToken tokenResolver) {
        log.info("Init TokenX token exchange.");
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
                .bodyToMono(WellKnown.class)
                .flatMap(config ->
                        webClient
                                .post()
                                .uri(config.getToken_endpoint())
                                .body(BodyInserters
                                        .fromFormData("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange")
                                        .with("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer")
                                        .with("client_assertion", createClientAssertion(config.getToken_endpoint()))
                                        .with("subject_token_type", "urn:ietf:params:oauth:token-type:jwt")
                                        .with("subject_token", token.getValue())
                                        .with("audience", toScope(serverProperties))
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

    @Override
    public ResourceServerType getType() {
        return ResourceServerType.TOKEN_X;
    }
}
