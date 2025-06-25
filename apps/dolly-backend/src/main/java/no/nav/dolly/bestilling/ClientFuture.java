package no.nav.dolly.bestilling;

import no.nav.dolly.domain.jpa.BestillingProgress;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

@FunctionalInterface
public interface ClientFuture extends Supplier<Mono<BestillingProgress>> {
}
