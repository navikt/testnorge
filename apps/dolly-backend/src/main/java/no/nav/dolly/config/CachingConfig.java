package no.nav.dolly.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.TimeUnit;

@Configuration(enforceUniqueMethods = false)
@EnableCaching
@SuppressWarnings("java:S3740")
public class CachingConfig {

    public static final String CACHE_BESTILLING = "bestilling";
    public static final String CACHE_BRUKER = "bruker";
    public static final String CACHE_GRUPPE = "gruppe";
    public static final String CACHE_HELSEPERSONELL = "helsepersonell";
    public static final String CACHE_KODEVERK = "kodeverk";
    public static final String CACHE_KODEVERK_2 = "kodeverk2";

    @Bean
    @Profile({ "dev", "prod" })
    public CacheManager cacheManager(Caffeine caffeine) {
        var caffeineCacheManager = new CaffeineCacheManager(CACHE_BESTILLING,
                CACHE_BRUKER,
                CACHE_GRUPPE,
                CACHE_HELSEPERSONELL,
                CACHE_KODEVERK,
                CACHE_KODEVERK_2
        );
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    @Bean
    @Profile("local")
    public CacheManager cacheManager() {
        return new NoOpCacheManager();
    }

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder().expireAfterWrite(8, TimeUnit.HOURS);
    }
}