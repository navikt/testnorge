package no.nav.registre.testnorge.person.domain;

import lombok.RequiredArgsConstructor;

import no.nav.registre.testnorge.dto.person.v1.PersonDTO;

@RequiredArgsConstructor
public class Person {
    private final PersonDTO dto;


    public String getIdent() {
        return dto.getIdent();
    }

    public String getFornavn() {
        return dto.getFornavn();
    }

    public String getMellomnavn() {
        return dto.getMellomnavn();
    }

    public String getEtternavn() {
        return dto.getEtternavn();
    }

    public Adresse getAdresse() { return new Adresse(dto.getAdresse());}

}
