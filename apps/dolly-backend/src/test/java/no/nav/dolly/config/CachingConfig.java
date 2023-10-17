package no.nav.dolly.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@EnableCaching
public class CachingConfig {
    @Bean
    @Profile("test")
    public CacheManager cacheManager() {
        return new NoOpCacheManager();
    }
}