package no.nav.dolly.proxy.auth;

import com.auth0.jwt.JWT;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@RequiredArgsConstructor
@Slf4j
class TokenCache {

    private final Cache<ServerProperties, Mono<String>> cache = Caffeine
            .newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .maximumSize(100)
            .build();
    private final long gracePeriodSeconds;

    Mono<String> get(ServerProperties serverProperties, Function<ServerProperties, Mono<AccessToken>> tokenExchange) {
        return cache.get(serverProperties, key ->
                tokenExchange
                        .apply(key)
                        .map(AccessToken::getTokenValue)
                        .cache(
                                token -> getTTL(key, token),
                                error -> Duration.ZERO,
                                () -> Duration.ZERO
                        )
        );
    }

    private Duration getTTL(ServerProperties key, String token) {
        try {
            var expiry = JWT
                    .decode(token)
                    .getExpiresAt()
                    .toInstant();
            var ttl = Duration
                    .between(Instant.now(), expiry)
                    .minusSeconds(gracePeriodSeconds);
            var nonNegativeTTL = ttl.isNegative() ? Duration.ZERO : ttl;
            log.info("Token cached for {} with TTL of {} seconds", key, nonNegativeTTL.getSeconds());
            return nonNegativeTTL;
        } catch (Exception e) {
            log.warn("Failed to decode token for {} to calculate TTL, disabling cache", key, e);
            return Duration.ZERO;
        }
    }

}
