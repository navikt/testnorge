package no.nav.registre.testnorge.personsearchservice.domain;

import lombok.Value;

import java.util.List;

@Value
public class PersonList {
    Long numberOfItems;
    List<Person> list;
}
