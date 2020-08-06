package no.nav.registre.testnorge.person.adapter;

import no.nav.registre.testnorge.person.domain.Person;

public interface PersonAdapter {
    void createPerson(Person person);

    Person getPerson(String ident);
    Person getPerson(String ident, String miljoe);
}
