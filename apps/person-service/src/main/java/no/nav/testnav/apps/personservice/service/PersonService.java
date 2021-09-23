package no.nav.testnav.apps.personservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.personservice.adapter.PdlPersonAdapter;
import no.nav.testnav.apps.personservice.adapter.TpsPersonAdapter;
import no.nav.testnav.apps.personservice.consumer.PdlTestdataConsumer;
import no.nav.testnav.apps.personservice.consumer.dto.pdl.graphql.PdlAktoer.AktoerIdent;
import no.nav.testnav.apps.personservice.domain.Person;
import no.nav.testnav.libs.dto.personservice.v1.Persondatasystem;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

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

    public Mono<Optional<AktoerIdent>> getAktoerId(String ident) {
        return pdlPersonAdapter.getAktoer(ident);
    }
}
