package no.nav.registre.testnorge.person.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import no.nav.registre.testnorge.person.adapter.PdlPersonAdapter;
import no.nav.registre.testnorge.person.domain.Person;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PdlPersonAdapter pdlPersonAdapter;

    public void createPerson(Person person) {
        pdlPersonAdapter.createPerson(person);
    }
}
