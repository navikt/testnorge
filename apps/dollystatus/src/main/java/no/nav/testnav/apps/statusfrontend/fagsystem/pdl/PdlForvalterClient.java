package no.nav.testnav.apps.statusfrontend.fagsystem.pdl;

import reactor.core.publisher.Mono;

public interface PdlForvalterClient {

    Mono<Boolean> personExists();

    Mono<Void> createPerson();

    Mono<Void> updateName();

    Mono<PdlOrderResponse> sendOrder();

    Mono<Void> deletePerson();
}
