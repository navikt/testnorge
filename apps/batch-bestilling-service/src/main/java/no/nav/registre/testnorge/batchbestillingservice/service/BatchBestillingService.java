package no.nav.registre.testnorge.batchbestillingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.batchbestillingservice.consumer.DollyBackendConsumer;
import no.nav.registre.testnorge.batchbestillingservice.request.RsDollyBestillingRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class BatchBestillingService {

    private final DollyBackendConsumer dollyBackendConsumer;

    public Mono<Void> sendBestillinger(Long gruppeId, RsDollyBestillingRequest request, Long antallPerBatch, Integer antallBatchJobber, Integer delayInMinutes) {
        AtomicInteger antallJobberFerdig = new AtomicInteger(0);

        return Flux.interval(Duration.ZERO, Duration.ofMinutes(delayInMinutes))
                .take(antallBatchJobber)
                .concatMap(tick -> executeIfNoActiveOrders(gruppeId, request, antallPerBatch, antallJobberFerdig, antallBatchJobber))
                .then();
    }

    private Mono<Void> executeIfNoActiveOrders(Long gruppeId, RsDollyBestillingRequest request, Long antallPerBatch, AtomicInteger antallJobberFerdig, Integer antallBatchJobber) {
        return dollyBackendConsumer.getAktiveBestillinger(gruppeId)
                .collectList()
                .flatMap(aktiveBestillinger -> {
                    if (!aktiveBestillinger.isEmpty()) {
                        log.warn("Gruppe {} har aktive bestillinger kjørende, venter til neste kjøring.", gruppeId);
                        return Mono.empty();
                    }

                    return dollyBackendConsumer.postDollyBestilling(gruppeId, request, antallPerBatch)
                            .doOnSuccess(v -> {
                                int ferdig = antallJobberFerdig.incrementAndGet();
                                log.info("antall jobber ferdig {}/{}", ferdig, antallBatchJobber);
                                if (ferdig >= antallBatchJobber) {
                                    log.info("Stopper batchjobb etter {} kjøringer", ferdig);
                                }
                            });
                });
    }
}
