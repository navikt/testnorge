package no.nav.testnav.apps.personservice.adapter;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.personservice.consumer.PdlApiConsumer;
import no.nav.testnav.apps.personservice.consumer.dto.pdl.graphql.PdlAktoer;
import no.nav.testnav.apps.personservice.domain.Person;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;


@Component
@RequiredArgsConstructor
public class PdlPersonAdapter implements PersonAdapter {
    private final PdlApiConsumer pdlApiConsumer;

    @Override
    public Mono<Optional<Person>> getPerson(String ident, String miljoe) {
        return pdlApiConsumer.getPerson(ident);
    }

    public Mono<Optional<PdlAktoer.AktoerIdent>> getAktoer(String ident) {
        return pdlApiConsumer.getAktoer(ident);
    }
}
