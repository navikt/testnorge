package no.nav.testnav.dollysearchservice.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;
import java.util.List;

@Configuration(enforceUniqueMethods = false)
@EnableCaching
@SuppressWarnings("java:S3740")
public class CachingConfig {

    public static final String CACHE_REGISTRE = "registre";
    public static final String CACHE_TESTNORGE_IDENTER = "testnorge_identer";

    @Bean
    @Profile({ "dev", "prod"})
    public CacheManager cacheManager() {
        var cacheManager = new SimpleCacheManager();

        // Define individual caches with different timeout values
        var shortTermCache = new CaffeineCache(CACHE_TESTNORGE_IDENTER,
                Caffeine.newBuilder()
                        .expireAfterAccess(Duration.ofMinutes(5))
                        .build());

        var longTermCache = new CaffeineCache(CACHE_REGISTRE,
                Caffeine.newBuilder()
                        .expireAfterAccess(Duration.ofHours(8))
                        .build());

        cacheManager.setCaches(List.of(shortTermCache, longTermCache));
        return cacheManager;
    }

    @Bean
    @Profile("local")
    public CacheManager cacheManagerLocal() {
        return new NoOpCacheManager();
    }
}