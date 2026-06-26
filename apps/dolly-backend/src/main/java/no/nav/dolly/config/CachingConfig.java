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

@Configuration
@EnableCaching
@SuppressWarnings("java:S3740")
public class CachingConfig {

    public static final String CACHE_BESTILLING = "bestilling";
    public static final String CACHE_BESTILLING_MAL = "bestilling-mal";
    public static final String CACHE_BRUKER = "bruker";
    public static final String CACHE_DASHBOARD_DOLLYTEAMS = "dashboard-dollyteams";
    public static final String CACHE_DASHBOARD_ORGANISASJONER = "dashboard-organisasjoner";
    public static final String CACHE_DASHBOARD_OVERSIKT = "dashboard-oversikt";
    public static final String CACHE_DASHBOARD_TEAMS = "dashboard-teams";
    public static final String CACHE_GRUPPE = "gruppe";
    public static final String CACHE_LEGACY_BESTILLING_MAL = "bestilling-legacy-mal";

    @Bean
    @Profile({ "dev", "prod" })
    public CacheManager cacheManager(Caffeine caffeine) {
        var caffeineCacheManager = new CaffeineCacheManager(
                CACHE_BESTILLING,
                CACHE_BESTILLING_MAL,
                CACHE_BRUKER,
                CACHE_DASHBOARD_DOLLYTEAMS,
                CACHE_DASHBOARD_ORGANISASJONER,
                CACHE_DASHBOARD_OVERSIKT,
                CACHE_DASHBOARD_TEAMS,
                CACHE_GRUPPE,
                CACHE_LEGACY_BESTILLING_MAL
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
        return Caffeine.newBuilder().expireAfterWrite(8, TimeUnit.HOURS);
    }
}