package no.nav.testnav.apps.importpersonservice.domain;

import no.nav.testnav.apps.importpersonservice.controller.dto.PersonListDTO;

import java.util.List;

public class PersonList {

    private final List<Person> list;

    public PersonList(PersonListDTO dto) {
        this.list = dto.getPersonList().stream().map(Person::new)
                .toList();
    }

    public PersonList(List<String> identer) {
        this.list = identer.stream().map(Person::new)
                .toList();
    }

    public List<Person> getList() {
        return list;
    }
}
