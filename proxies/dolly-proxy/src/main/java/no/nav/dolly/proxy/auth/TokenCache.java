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

@RequiredArgsConstructor
@Slf4j
class TokenCache {

    private final Map<ServerProperties, Mono<String>> cache = new ConcurrentHashMap<>();

    private final long gracePeriodSeconds;

    Mono<String> get(ServerProperties serverProperties, Function<ServerProperties, Mono<AccessToken>> tokenExchange) {
        return cache.computeIfAbsent(serverProperties, key ->
                tokenExchange.apply(key)
                        .map(AccessToken::getTokenValue)
                        .cache(
                                this::getTTL,
                                error -> Duration.ZERO,
                                () -> Duration.ZERO
                        )
                        .doOnNext(token -> log.debug("Token cached for {}", key))
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
            return ttl.isNegative() ? Duration.ZERO : ttl;
        } catch (Exception e) {
            log.warn("Failed to decode token to calculate TTL, disabling cache", e);
            return Duration.ZERO;
        }
    }

}

