package no.nav.testnav.apps.statusfrontend.fagsystem.inntektstub;

import reactor.core.publisher.Mono;

public interface InntektstubClient {

    Mono<InntektstubResourceStatus> getIncome(InntektstubRequest expectedRequest);

    Mono<Void> createIncome(InntektstubRequest request);

    Mono<Void> deleteIncome(String ident);
}
