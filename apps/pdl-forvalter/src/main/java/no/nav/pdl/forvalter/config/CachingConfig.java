package no.nav.pdl.forvalter.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;

@Configuration
@EnableCaching
@SuppressWarnings("java:S3740")
public class CachingConfig {

    public static final String CACHE_IDENTER = "identer";

    @Bean
    @Profile({ "dev", "prod"})
    public CacheManager cacheManager() {
        var cacheManager = new CaffeineCacheManager();
        cacheManager.setAsyncCacheMode(true);

        // Define individual caches with different timeout values
        cacheManager.registerCustomCache(CACHE_IDENTER,
                Caffeine.newBuilder()
                        .expireAfterAccess(Duration.ofMinutes(5))
                        .buildAsync());

        return cacheManager;
    }

    @Bean
    @Profile("local")
    public CacheManager cacheManagerLocal() {
        return new NoOpCacheManager();
    }
}