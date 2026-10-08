package no.nav.testnav.apps.statusfrontend.fagsystem.udi;

import reactor.core.publisher.Mono;

public interface UdiClient {

    Mono<UdiResourceStatus> getPerson(UdiRequest expectedRequest);

    Mono<Void> createPerson(UdiRequest request);

    Mono<Void> deletePerson(String ident);
}
