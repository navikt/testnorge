package no.nav.organisasjonforvalter.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String CACHE_EREG_IMPORT = "org-importert";
    public static final String CACHE_BEDRIFT = "underenheter";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(CACHE_EREG_IMPORT, CACHE_BEDRIFT);
    }
}
