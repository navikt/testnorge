package no.nav.testnav.apps.personservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import no.nav.testnav.apps.personservice.consumer.PdlTestdataConsumer;
import reactor.core.publisher.Mono;

import java.util.Optional;

import no.nav.testnav.apps.personservice.adapter.PdlPersonAdapter;
import no.nav.testnav.apps.personservice.adapter.TpsPersonAdapter;
import no.nav.testnav.apps.personservice.domain.Person;
import no.nav.testnav.libs.dto.personservice.v1.Persondatasystem;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PdlPersonAdapter pdlPersonAdapter;
    private final TpsPersonAdapter tpsPersonAdapter;
    private final PdlTestdataConsumer pdlTestdataConsumer;

    public String createPerson(Person person, String kilde) {
        return pdlTestdataConsumer.createPerson(person, kilde);
    }

    public Mono<Optional<Person>> getPerson(String ident, String miljoe, Persondatasystem persondatasystem) {
        if (persondatasystem.equals(Persondatasystem.PDL)) {
            return pdlPersonAdapter.getPerson(ident, miljoe);
        } else {
            return tpsPersonAdapter.getPerson(ident, miljoe);
        }
    }
}
