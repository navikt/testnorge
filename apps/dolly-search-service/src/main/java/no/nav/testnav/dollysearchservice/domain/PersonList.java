package no.nav.testnav.dollysearchservice.domain;

import lombok.Value;

import java.util.List;

@Value
public class PersonList {
    Long numberOfItems;
    List<Person> list;
}
