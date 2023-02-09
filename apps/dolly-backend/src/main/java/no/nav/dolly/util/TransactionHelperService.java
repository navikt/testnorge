package no.nav.dolly.util;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import java.util.function.BiConsumer;

import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;

@Service
public class TransactionHelperService {

    private final TransactionTemplate transactionTemplate;
    private final EntityManager entityManager;
    private final CacheManager cacheManager;

    public TransactionHelperService(PlatformTransactionManager transactionManager, EntityManager entityManager, CacheManager cacheManager) {

        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.entityManager = entityManager;
        this.cacheManager = cacheManager;
    }

    @SuppressWarnings("java:S1143")
    public BestillingProgress oppdaterProgress(BestillingProgress progress) {

        return transactionTemplate.execute(status -> {
            var best = entityManager.find(Bestilling.class, progress.getBestilling().getId());
            entityManager.persist(progress);
            best.setSistOppdatert(now());
            entityManager.merge(best);
            clearCache();
            return progress;
        });
    }

    public BestillingProgress persister(BestillingProgress bestillingProgress, BiConsumer<BestillingProgress, String> setter, String status) {

        return transactionTemplate.execute(status1 -> {
            var progress = entityManager.find(BestillingProgress.class, bestillingProgress.getId());

            this.setField(progress, status, setter);
            entityManager.merge(progress);
            clearCache();
            return progress;
        });
    }

    @SuppressWarnings("java:S1143")
    public Bestilling oppdaterBestillingFerdig(Bestilling bestilling) {

        return transactionTemplate.execute(status -> {
            var best = entityManager.find(Bestilling.class, bestilling.getId());
            best.setSistOppdatert(now());
            best.setFerdig(true);
            best.setFeil(bestilling.getFeil());
            entityManager.merge(best);
            clearCache();
            return best;
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

    private <BestillingProgress, String> void setField(BestillingProgress obj, String value, BiConsumer<BestillingProgress, String> setter) {
        setter.accept(obj, value);
    }
}
