package no.nav.registre.testnorge.batchbestillingservice.command;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface ReactiveCommand<T> {
    Mono<T> execute();
}

