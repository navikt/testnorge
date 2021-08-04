package no.nav.testnav.apps.personservice.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

import no.nav.testnav.apps.personservice.consumer.TpsForvalterenConsumer;
import no.nav.testnav.apps.personservice.domain.Person;

@Component
@RequiredArgsConstructor
public class TpsPersonAdapter implements PersonAdapter {
    private final TpsForvalterenConsumer tpsForvalterenConsumer;

    @Override
    public Mono<Optional<Person>> getPerson(String ident, String miljoe) {
        return tpsForvalterenConsumer.getPerson(ident, miljoe);
    }
}
