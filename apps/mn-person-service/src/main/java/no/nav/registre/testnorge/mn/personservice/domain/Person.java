package no.nav.registre.testnorge.mn.personservice.domain;

import lombok.AllArgsConstructor;

import no.nav.registre.testnorge.libs.dto.person.v1.PersonDTO;

@AllArgsConstructor
public class Person {
    private final PersonDTO dto;

    public PersonDTO toDTO() {
        return dto;
    }
}
