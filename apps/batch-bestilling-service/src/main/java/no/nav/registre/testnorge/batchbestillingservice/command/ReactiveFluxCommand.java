package no.nav.registre.testnorge.batchbestillingservice.command;

import reactor.core.publisher.Flux;

@FunctionalInterface
public interface ReactiveFluxCommand<T> {
    Flux<T> execute();
}

