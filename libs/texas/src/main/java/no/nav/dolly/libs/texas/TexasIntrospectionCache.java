package no.nav.dolly.libs.texas;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

class TexasIntrospectionCache {

    private static final long CACHE_TTL_MINUTES = 10;

    private final AsyncCache<String, String> cache = Caffeine
            .newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(CACHE_TTL_MINUTES, TimeUnit.MINUTES)
            .buildAsync();

    Mono<String> get(String token, Function<String, Mono<String>> mappingFunction) {
        return Mono
                .fromFuture(
                        cache.get(token, (tok, executor) -> mappingFunction.apply(tok).toFuture())
                );
    }

}
