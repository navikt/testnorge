package no.nav.dolly.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CachingConfig {

    public static final String CACHE_BESTILLING = "bestilling";
    public static final String CACHE_BRUKER = "bruker";
    public static final String CACHE_GRUPPE = "gruppe";
    public static final String CACHE_KODEVERK = "kodeverk";
    public static final String CACHE_TEAM = "team";
    public static final String CACHE_NORG2 = "norg2";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(CACHE_BESTILLING, CACHE_BRUKER, CACHE_GRUPPE, CACHE_KODEVERK, CACHE_TEAM, CACHE_NORG2);
    }
}