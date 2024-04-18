package no.nav.dolly.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableCaching
@Profile("test")
public class TestCachingConfig {
    @Bean
    public CacheManager cacheManager() {
        return new NoOpCacheManager();
    }

}