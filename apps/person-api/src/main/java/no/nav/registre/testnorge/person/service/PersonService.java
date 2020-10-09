package no.nav.registre.testnorge.person.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import no.nav.registre.testnorge.person.adapter.PdlPersonAdapter;
import no.nav.registre.testnorge.person.adapter.TpsPersonAdapter;
import no.nav.registre.testnorge.person.domain.Person;
import no.nav.registre.testnorge.libs.dto.person.v1.Persondatasystem;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonService {

    private final PdlPersonAdapter pdlPersonAdapter;
    private final TpsPersonAdapter tpsPersonAdapter;

    public String createPerson(Person person, String kilde) {
        return pdlPersonAdapter.createPerson(person, kilde);
    }

    public Person getPerson(String ident, String miljoe, Persondatasystem persondatasystem) {

        if (persondatasystem.equals(Persondatasystem.PDL)) {
            return pdlPersonAdapter.getPerson(ident);
        } else {
            return tpsPersonAdapter.getPerson(ident, miljoe);
        }
    }
}
