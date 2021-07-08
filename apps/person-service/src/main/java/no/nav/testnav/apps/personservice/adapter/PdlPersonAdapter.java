package no.nav.testnav.apps.personservice.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

import no.nav.testnav.apps.personservice.consumer.PdlApiConsumer;
import no.nav.testnav.apps.personservice.domain.Person;


@Component
@RequiredArgsConstructor
public class PdlPersonAdapter implements PersonAdapter {
    private final PdlApiConsumer pdlApiConsumer;

    @Override
    public Mono<Optional<Person>> getPerson(String ident, String miljoe) {
        return pdlApiConsumer.getPerson(ident);
    }
}
