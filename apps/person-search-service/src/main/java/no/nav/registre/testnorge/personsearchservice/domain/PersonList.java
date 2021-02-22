package no.nav.registre.testnorge.personsearchservice.domain;

import lombok.Value;

import java.util.List;

@Value
public class PersonList {
    Integer numberOfPages;
    List<Person> list;
}
