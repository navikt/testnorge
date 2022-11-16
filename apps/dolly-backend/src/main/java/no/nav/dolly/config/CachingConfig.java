package no.nav.dolly.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableCaching
public class CachingConfig {

    public static final String CACHE_BESTILLING = "bestilling";
    public static final String CACHE_BRUKER = "bruker";
    public static final String CACHE_GRUPPE = "gruppe";
    public static final String CACHE_HELSEPERSONELL = "helsepersonell";
    public static final String CACHE_KODEVERK = "kodeverk";
    public static final String CACHE_KODEVERK_2 = "kodeverk2";

    @Bean
    @Profile({ "prod", "dev" })
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(CACHE_BESTILLING,
                CACHE_BRUKER,
                CACHE_GRUPPE,
                CACHE_HELSEPERSONELL,
                CACHE_KODEVERK,
                CACHE_KODEVERK_2
        );

    }

    @Bean
    @Profile("local")
    public CacheManager getNoOpCacheManager() {
        return new NoOpCacheManager();
    }
}