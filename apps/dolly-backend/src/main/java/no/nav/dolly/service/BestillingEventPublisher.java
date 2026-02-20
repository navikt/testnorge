package no.nav.dolly.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Set;

@Slf4j
@Service
public class BestillingEventPublisher {

    private static final int BUFFER_SIZE = 256;
    private final Sinks.Many<Long> sink = Sinks.many().multicast().onBackpressureBuffer(BUFFER_SIZE, false);

    public void publish(Long bestillingId) {
        var result = sink.tryEmitNext(bestillingId);
        if (result.isFailure()) {
            log.warn("Kunne ikke publisere bestilling-event for bestillingId {}: {}", bestillingId, result);
        }
    }

    public Flux<Long> subscribe(Long bestillingId) {
        return sink.asFlux()
                .filter(bestillingId::equals);
    }

    public Flux<Long> subscribeAny(Set<Long> bestillingIds) {
        return sink.asFlux()
                .filter(bestillingIds::contains);
    }
}
