package no.nav.dolly.libs.texas;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

class TexasTokenCache {

    private final AsyncCache<String, TexasToken> cache;

    TexasTokenCache() {
        cache = Caffeine
                .newBuilder()
                .expireAfter(new Expiry<String, TexasToken>() {
                    @Override
                    public long expireAfterCreate(String key, TexasToken token, long currentTime) {
                        return TimeUnit.SECONDS.toNanos(Long.parseLong(token.expires_in()));
                    }

                    @Override
                    public long expireAfterUpdate(String key, TexasToken token, long currentTime, long currentDuration) {
                        return currentDuration;
                    }

                    @Override
                    public long expireAfterRead(String key, TexasToken token, long currentTime, long currentDuration) {
                        return currentDuration;
                    }
                })
                .buildAsync();
    }

    Mono<TexasToken> get(String audience, TexasTokenLoader loader) {
        var future = cache.get(audience, (key, executor) -> loader.load(key).toFuture());
        return Mono.fromFuture(future);
    }

    long estimatedSize() {
        return cache.synchronous().estimatedSize();
    }

    interface TexasTokenLoader {
        Mono<TexasToken> load(String audience);
    }

}
