package no.nav.dolly.proxy.auth;

import com.auth0.jwt.JWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Slf4j
class TokenCache {

    private final Map<Object, Mono<String>> cache = new ConcurrentHashMap<>();

    private final long gracePeriodSeconds;

    Mono<String> get(ServerProperties serverProperties, Function<ServerProperties, Mono<AccessToken>> tokenExchange) {
        return get(serverProperties, () -> tokenExchange.apply(serverProperties));
    }

    Mono<String> get(Object key, Supplier<Mono<AccessToken>> tokenExchange) {
        return cache.computeIfAbsent(key, value ->
                tokenExchange
                        .get()
                        .map(AccessToken::getTokenValue)
                        .cache(
                                this::getTTL,
                                error -> Duration.ZERO,
                                () -> Duration.ZERO
                        )
        );
    }

    private Duration getTTL(String tokenValue) {
        try {
            var expiry = JWT
                    .decode(tokenValue)
                    .getExpiresAt()
                    .toInstant();
            var ttl = Duration
                    .between(Instant.now(), expiry)
                    .minusSeconds(gracePeriodSeconds);
            var nonNegativeTTL = ttl.isNegative() ? Duration.ZERO : ttl;
            log.info("Token cached for {} with TTL {} seconds", tokenValue, nonNegativeTTL.getSeconds());
            return nonNegativeTTL;
        } catch (Exception e) {
            log.warn("Failed to decode token to calculate TTL, disabling cache", e);
            return Duration.ZERO;
        }
    }

}
