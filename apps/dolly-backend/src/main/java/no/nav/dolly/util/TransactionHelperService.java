package no.nav.dolly.util;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.service.BestillingService;
import org.springframework.cache.CacheManager;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.BiConsumer;
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

    private final TransactionalOperator transactionalOperator;
    private final CacheManager cacheManager;
    private final BestillingRepository bestillingRepository;
    private final BestillingProgressRepository bestillingProgressRepository;
    private final BestillingService bestillingService;

    public TransactionHelperService(R2dbcTransactionManager transactionManager,
                                    CacheManager cacheManager,
                                    BestillingRepository bestillingRepository,
                                    BestillingProgressRepository bestillingProgressRepository,
                                    BestillingService bestillingService) {

        this.transactionalOperator = TransactionalOperator.create(transactionManager);
        this.cacheManager = cacheManager;
        this.bestillingRepository = bestillingRepository;
        this.bestillingProgressRepository = bestillingProgressRepository;
        this.bestillingService = bestillingService;
    }

    @Retryable
    public Mono<BestillingProgress> persister(BestillingProgress bestillingProgress, BiConsumer<BestillingProgress, String> setter, String status) {

        return transactionalOperator.execute(status1 ->

                        bestillingProgressRepository.findByIdAndLock(bestillingProgress.getId())
                                .flatMap(progress -> {
                                    setter.accept(progress, status);
                                    return bestillingProgressRepository.save(progress);
                                })
                                .doFinally(signal -> clearCache()))
                .collectList()
                .map(list -> list.isEmpty() ? null : list.getFirst());
    }

    public Mono<BestillingProgress> persister(BestillingProgress bestillingProgress,
                                              Function<BestillingProgress, String> getter,
                                              BiConsumer<BestillingProgress, String> setter, String status) {

        return persister(bestillingProgress, getter, setter, status, null);
    }

    @Retryable
    public Mono<BestillingProgress> persister(BestillingProgress bestillingProgress,
                                              Function<BestillingProgress, String> getter,
                                              BiConsumer<BestillingProgress, String> setter, String status,
                                              String separator) {

        return transactionalOperator.execute(status1 ->

                        bestillingProgressRepository.findByIdAndLock(bestillingProgress.getId())
                                .doOnNext(progress -> {
                                    var value = getter.apply(progress);
                                    var result = applyChanges(value, status, separator);
                                    setter.accept(progress, result);
                                })
                                .flatMap(bestillingProgressRepository::save)
                                .doFinally(signal -> clearCache()))
                .collectList()
                .map(list -> list.isEmpty() ? null : list.getFirst());
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
    public Mono<String> getProgress(BestillingProgress bestillingProgress, Function<BestillingProgress, String> getter) {

        return bestillingProgressRepository.findById(bestillingProgress.getId())
                .map(getter);
    }

    @Retryable
    public Mono<Bestilling> persister(Long bestillingId, RsDollyBestilling bestilling) {

        return transactionalOperator.execute(status ->

                        bestillingService.getBestKriterier(bestilling)
                                .flatMap(kriterier ->
                                        bestillingRepository.findByIdAndLock(bestillingId)
                                                .doOnNext(best -> {
                                                    bestilling.setId(bestillingId);
                                                    best.setBestKriterier(kriterier);
                                                })
                                                .flatMap(bestillingRepository::save)))
                .collectList()
                .map(list -> list.isEmpty() ? null : list.getFirst());
    }

    @Retryable
    public Mono<Bestilling> oppdaterBestillingFerdig(Bestilling bestilling) {

        return transactionalOperator.execute(status ->

                        bestillingRepository.findByIdAndLock(bestilling.getId())
                                .doOnNext(bestilling1 -> {
                                    bestilling1.setSistOppdatert(now());
                                    bestilling1.setFerdig(true);
                                })
                                .flatMap(bestillingRepository::save)
                                .doFinally(signal -> clearCache()))
                .collectList()
                .map(list -> list.isEmpty() ? null : list.getFirst());
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
