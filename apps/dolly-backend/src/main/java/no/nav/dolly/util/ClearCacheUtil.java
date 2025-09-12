package no.nav.dolly.util;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;

@RequiredArgsConstructor
public class ClearCacheUtil implements Runnable {

    private final CacheManager cacheManager;

    @Override
    public void run() {

        if (nonNull(cacheManager.getCache(CACHE_BESTILLING))) {
            requireNonNull(cacheManager.getCache(CACHE_BESTILLING)).clear();
        }
        if (nonNull(cacheManager.getCache(CACHE_GRUPPE))) {
            requireNonNull(cacheManager.getCache(CACHE_GRUPPE)).clear();
        }
    }
}
