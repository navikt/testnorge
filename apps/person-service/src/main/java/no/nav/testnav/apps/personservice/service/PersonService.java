package no.nav.testnav.apps.personservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.personservice.consumer.v1.PdlApiConsumer;
import no.nav.testnav.apps.personservice.consumer.v1.PdlTestdataConsumer;
import no.nav.testnav.apps.personservice.consumer.v1.pdl.graphql.PdlAktoer.AktoerIdent;
import no.nav.testnav.apps.personservice.domain.Person;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PdlApiConsumer pdlApiConsumer;
    private final PdlTestdataConsumer pdlTestdataConsumer;

    public Mono<String> ordrePerson(Person person, String kilde) {

        return pdlTestdataConsumer.ordrePerson(person, kilde);
    }

    public Mono<Optional<Person>> getPerson(String ident) {

            return pdlApiConsumer.getPerson(ident);
    }

    public Mono<Optional<AktoerIdent>> getAktoerId(String ident) {
        return pdlApiConsumer.getAktoer(ident);
    }

    public Mono<Boolean> isPerson(String ident, Set<String> opplysningId) {

        return pdlApiConsumer.isPerson(ident, opplysningId);
    }
}