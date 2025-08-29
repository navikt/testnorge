package no.nav.dolly.util;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.service.BestillingService;
import org.springframework.cache.CacheManager;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;
import static no.nav.dolly.util.DollyTextUtil.containsInfoText;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class TransactionHelperService {

    private final TransactionTemplate transactionTemplate;
    private final CacheManager cacheManager;
    private final BestillingRepository bestillingRepository;
    private final BestillingProgressRepository bestillingProgressRepository;
    private final BestillingService bestillingService;

    public TransactionHelperService(PlatformTransactionManager transactionManager,
                                    CacheManager cacheManager,
                                    BestillingRepository bestillingRepository,
                                    BestillingProgressRepository bestillingProgressRepository,
                                    BestillingService bestillingService) {

        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.cacheManager = cacheManager;
        this.bestillingRepository = bestillingRepository;
        this.bestillingProgressRepository = bestillingProgressRepository;
        this.bestillingService = bestillingService;
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

            var akkumulert = new AtomicReference<>(bestillingProgress);

            bestillingProgressRepository.findByIdAndLock(bestillingProgress.getId())
                    .ifPresent(progress -> {
                        setter.accept(progress, status);
                        akkumulert.set(bestillingProgressRepository.save(progress));
                        clearCache();
                    });

            return akkumulert.get();
        });
    }

    public BestillingProgress persister(BestillingProgress bestillingProgress,
                                        Function<BestillingProgress, String> getter,
                                        BiConsumer<BestillingProgress, String> setter, String status) {

        return persister(bestillingProgress, getter, setter, status, null);
    }

    @Retryable
    public BestillingProgress persister(BestillingProgress bestillingProgress,
                                        Function<BestillingProgress, String> getter,
                                        BiConsumer<BestillingProgress, String> setter, String status,
                                        String separator) {

        return transactionTemplate.execute(status1 -> {

            var akkumulert = new AtomicReference<>(bestillingProgress);

            bestillingProgressRepository.findByIdAndLock(bestillingProgress.getId())
                    .ifPresent(progress -> {
                        var value = getter.apply(progress);
                        var result = applyChanges(value, status, separator);
                        setter.accept(progress, result);
                        akkumulert.set(bestillingProgressRepository.save(progress));
                        clearCache();
                    });

            return akkumulert.get();
        });
    }

    @Retryable
    public BestillingProgress persisterDynamicProgress(BestillingProgress bestillingProgress,
                                                       Function<BestillingProgress, String> getter,
                                                       BiConsumer<BestillingProgress, String> setter,
                                                       String status) {

        return transactionTemplate.execute(status1 -> {

            var akkumulert = new AtomicReference<>(bestillingProgress);

            bestillingProgressRepository.findByIdAndLock(bestillingProgress.getId())
                    .ifPresent(progress -> {
                        var value = getter.apply(progress);
                        var result = applyChanges(value, status);
                        setter.accept(progress, result);
                        akkumulert.set(bestillingProgressRepository.save(progress));
                        clearCache();
                    });

            return akkumulert.get();
        });
    }

    private String applyChanges(String value, String status, String separator) {

        if (isBlank(value)) {
            return status;

        } else {
            var regex = nonNull(separator) ? separator : ",";

            return Stream.of(status.split(regex),
                            value.split(regex))
                    .flatMap(Arrays::stream)
                    .filter(text -> !containsInfoText(text))
                    .distinct()
                    .collect(Collectors.joining(regex));
        }
    }

    private String applyChanges(String gmlStatus, String nyStatus) {

        if (isBlank(gmlStatus)) {
            return nyStatus;

        } else {

            var nyeStatuser = Arrays.stream(nyStatus.split(","))
                    .filter(status -> status.split(":").length > 1)
                    .collect(Collectors.toMap(data -> data.split(":")[0], data -> data.split(":")[1]));
            var gamleStatuser = Arrays.stream(gmlStatus.split(","))
                    .filter(status -> status.split(":").length > 1)
                    .collect(Collectors.toMap(data -> data.split(":")[0], data -> data.split(":")[1]));

            var resultater = new HashMap<>(gamleStatuser);
            resultater.putAll(nyeStatuser);

            return resultater.entrySet().stream()
                    .map(data -> "%s:%s".formatted(data.getKey(), data.getValue()))
                    .collect(Collectors.joining(","));
        }
    }

    @Retryable
    public String getProgress(BestillingProgress bestillingProgress, Function<BestillingProgress, String> getter) {

        var status = new AtomicReference<String>(null);
        bestillingProgressRepository.findById(bestillingProgress.getId())
                .ifPresent(progress ->
                        status.set(getter.apply(progress)));

        return status.get();
    }

    @Retryable
    public Bestilling persister(Long bestillingId, RsDollyBestilling bestilling) {

        return transactionTemplate.execute(status -> {

            var akkumulert = new AtomicReference<Bestilling>(null);
            bestillingRepository.findByIdAndLock(bestillingId)
                    .ifPresent(best -> {
                        bestilling.setId(bestillingId);
                        best.setBestKriterier(bestillingService.getBestKriterier(bestilling));
                        akkumulert.set(bestillingRepository.save(best));
                    });

            return akkumulert.get();
        });
    }

    @Retryable
    public Bestilling oppdaterBestillingFerdig(Long id, Consumer<Bestilling> bestillingFunksjon) {

        return transactionTemplate.execute(status -> {

            var akkumulert = new AtomicReference<Bestilling>(null);

            bestillingRepository.findByIdAndLock(id)
                    .ifPresent(bestilling -> {
                        bestilling.setSistOppdatert(now());
                        bestilling.setFerdig(true);
                        bestillingFunksjon.accept(bestilling);
                        akkumulert.set(bestillingRepository.save(bestilling));
                        clearCache();
                    });

            return akkumulert.get();
        });
    }

    public void clearCache() {
        if (nonNull(cacheManager.getCache(CACHE_BESTILLING))) {
            requireNonNull(cacheManager.getCache(CACHE_BESTILLING)).clear();
        }
        if (nonNull(cacheManager.getCache(CACHE_GRUPPE))) {
            requireNonNull(cacheManager.getCache(CACHE_GRUPPE)).clear();
        }
    }
}
