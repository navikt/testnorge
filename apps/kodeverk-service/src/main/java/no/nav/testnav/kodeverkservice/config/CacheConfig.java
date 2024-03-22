package no.nav.testnav.kodeverkservice.config;

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
public class CacheConfig {

    public static final String CACHE_KODEVERK = "kodeverk";

    @Bean
    @Profile("prod")
    public CacheManager cacheManager(Caffeine caffeine) {

        var caffeineCacheManager = new CaffeineCacheManager(CACHE_KODEVERK);
        caffeineCacheManager.setCaffeine(caffeine);
        caffeineCacheManager.setAsyncCacheMode(true);
        return caffeineCacheManager;
    }

    @Bean
    @Profile("dev")
    public CacheManager getNoOpCacheManager() {

        return new NoOpCacheManager();
    }

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {

        return Caffeine.newBuilder().expireAfterWrite(12, TimeUnit.HOURS);
    }
}