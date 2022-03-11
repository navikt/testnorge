package no.nav.dolly.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CachingConfig {

    public static final String CACHE_BESTILLING = "bestilling";
    public static final String CACHE_BRUKER = "bruker";
    public static final String CACHE_GRUPPE = "gruppe";
    public static final String CACHE_KODEVERK = "kodeverk";
    public static final String CACHE_KODEVERK_2 = "kodeverk2";
    public static final String CACHE_NOMDATA = "nom";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(CACHE_BESTILLING,
                CACHE_BRUKER,
                CACHE_GRUPPE,
                CACHE_KODEVERK,
                CACHE_KODEVERK_2,
                CACHE_NOMDATA
        );
    }
}