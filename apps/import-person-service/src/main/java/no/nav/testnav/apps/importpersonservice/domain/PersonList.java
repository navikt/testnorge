package no.nav.testnav.apps.importpersonservice.domain;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.testnav.apps.importpersonservice.controller.dto.PersonListDTO;

public class PersonList {

    private final List<Person> list;

    public PersonList(PersonListDTO dto) {
        this.list = dto.getPersonList().stream().map(Person::new).collect(Collectors.toList());
    }

    public PersonList(List<String> identer) {
        this.list = identer.stream().map(Person::new).collect(Collectors.toList());
    }

    public List<Person> getList() {
        return list;
    }
}
