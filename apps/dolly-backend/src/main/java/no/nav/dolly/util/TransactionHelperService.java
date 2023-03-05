package no.nav.dolly.util;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import org.springframework.cache.CacheManager;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;

@Service
public class TransactionHelperService {

    private final TransactionTemplate transactionTemplate;
    private final CacheManager cacheManager;
    private final BestillingRepository bestillingRepository;
    private final BestillingProgressRepository bestillingProgressRepository;

    public TransactionHelperService(PlatformTransactionManager transactionManager, CacheManager cacheManager,
                                    BestillingRepository bestillingRepository,
                                    BestillingProgressRepository bestillingProgressRepository) {

        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.cacheManager = cacheManager;
        this.bestillingRepository = bestillingRepository;
        this.bestillingProgressRepository = bestillingProgressRepository;
    }

    @Retryable
    public BestillingProgress opprettProgress(BestillingProgress progress) {

        return transactionTemplate.execute(status -> {
            bestillingRepository.findByIdAndLock(progress.getBestilling().getId())
                    .ifPresent(bestilling -> {
                        bestilling.setSistOppdatert(now());
                        bestillingRepository.save(bestilling);
                        bestillingProgressRepository.save(progress);
                    });
            clearCache();
            return progress;
        });
    }

    @Retryable
    public BestillingProgress persister(BestillingProgress bestillingProgress, BiConsumer<BestillingProgress, String> setter, String status) {

        return transactionTemplate.execute(status1 -> {

            bestillingProgressRepository.findByIdAndLock(bestillingProgress.getId())
                    .ifPresent(progress -> {
                        this.setField(progress, status, setter);
                        bestillingProgressRepository.save(progress);
                        clearCache();
                    });

            return bestillingProgress;
        });
    }

    @Retryable
    public Bestilling oppdaterBestillingFerdig(Long id, Consumer<Bestilling> bestillingFunksjon) {

        return transactionTemplate.execute(status -> {
            bestillingRepository.findByIdAndLock(id)
                    .ifPresent(bestilling -> {
                        bestilling.setSistOppdatert(now());
                        bestilling.setFerdig(true);
                        bestillingFunksjon.accept(bestilling);
                        bestillingRepository.save(bestilling);
                        clearCache();
                    });
            return null;
        });
    }

    private void clearCache() {
        if (nonNull(cacheManager.getCache(CACHE_BESTILLING))) {
            requireNonNull(cacheManager.getCache(CACHE_BESTILLING)).clear();
        }
        if (nonNull(cacheManager.getCache(CACHE_GRUPPE))) {
            requireNonNull(cacheManager.getCache(CACHE_GRUPPE)).clear();
        }
    }

    private <T, R> void setField(T object, R value, BiConsumer<T, R> setter) {

        setter.accept(object, value);
    }
}
