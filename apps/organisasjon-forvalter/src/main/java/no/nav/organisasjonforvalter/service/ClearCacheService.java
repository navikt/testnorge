package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static no.nav.organisasjonforvalter.config.CacheConfig.CACHE_MILJOER;

@Service
@RequiredArgsConstructor
public class ClearCacheService {

    private final static long INTERVAL = 60 * 60 * 1000L;

    private final CacheManager cacheManager;

    @Scheduled(fixedRate = INTERVAL)
    public void cacheEvictMiljoer() {
        cacheManager.getCache(CACHE_MILJOER).clear();
    }
}
