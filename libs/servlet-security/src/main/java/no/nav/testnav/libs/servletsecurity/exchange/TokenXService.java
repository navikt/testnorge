package no.nav.testnav.libs.servletsecurity.exchange;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

import no.nav.testnav.libs.servletsecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import no.nav.testnav.libs.servletsecurity.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.domain.ResourceServerType;
import no.nav.testnav.libs.servletsecurity.domain.TokenX;
import no.nav.testnav.libs.servletsecurity.domain.v1.WellKnown;
import no.nav.testnav.libs.servletsecurity.exchange.tokenx.command.GetAccessTokenCommand;
import no.nav.testnav.libs.servletsecurity.exchange.tokenx.command.GetWellKnownConfigCommand;


@Slf4j
@Service
@ConditionalOnProperty("spring.security.oauth2.resourceserver.tokenx.issuer-uri")
public class TokenXService implements TokenService {
    private final GetAuthenticatedToken getAuthenticatedTokenAction;
    private final WebClient webClient;
    private final TokenX tokenX;
    private Mono<WellKnown> wellKnownConfig;

    TokenXService(
            TokenX tokenX,
            GetAuthenticatedToken tokenResolver
    ) {
        log.info("Init TokenX token exchange.");
        this.webClient = WebClient.builder().build();
        this.tokenX = tokenX;
        this.getAuthenticatedTokenAction = tokenResolver;
    }

    private String toScope(ServerProperties serverProperties) {
        return serverProperties.getCluster() + ":" + serverProperties.getNamespace() + ":" + serverProperties.getName();
    }

    private static <T> Mono<T> cache(Mono<T> value, Function<? super T, Duration> ttlForValue) {
        return value.cache(
                ttlForValue,
                throwable -> Duration.ZERO,
                () -> Duration.ZERO
        );
    }


    private Mono<WellKnown> getWellKnownConfig() {
        if (wellKnownConfig == null) {
            wellKnownConfig = cache(
                    new GetWellKnownConfigCommand(webClient, tokenX.getWellKnownUrl()).call(),
                    wellKnown -> Duration.ofDays(7)
            );
        }
        return wellKnownConfig;
    }


    @Override
    public Mono<AccessToken> generateToken(ServerProperties serverProperties) {
        var token = getAuthenticatedTokenAction.call();
        return getWellKnownConfig()
                .flatMap(config -> new GetAccessTokenCommand(
                                webClient,
                                config.getToken_endpoint(),
                                createClientAssertion(config.getToken_endpoint()),
                                token.value(),
                                toScope(serverProperties)
                        ).call()
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
