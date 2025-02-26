package no.nav.testnav.dollysearchservice.config;

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

    public static final String CACHE_REGISTRE = "registre";

    @Bean
    @Profile({ "dev", "prod"})
    public CacheManager cacheManager(Caffeine caffeine) {
        var caffeineCacheManager = new CaffeineCacheManager(CACHE_REGISTRE
        );
        caffeineCacheManager.setCaffeine(caffeine);
        caffeineCacheManager.setAsyncCacheMode(true);
        return caffeineCacheManager;
    }

    @Bean
    @Profile("local")
    public CacheManager cacheManagerLocal() {
        return new NoOpCacheManager();
    }

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder().expireAfterWrite(12, TimeUnit.HOURS);
    }
}