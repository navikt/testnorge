package no.nav.testnav.apps.personservice.adapter;

import reactor.core.publisher.Mono;

import java.util.Optional;

import no.nav.testnav.apps.personservice.domain.Person;

public interface PersonAdapter {
    Mono<Optional<Person>> getPerson(String ident, String miljoe);
}
