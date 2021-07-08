package no.nav.registre.testnorge.synt.person.domain;

import no.nav.testnav.libs.dto.person.v1.AdresseDTO;
import no.nav.testnav.libs.dto.person.v1.PersonDTO;
import no.nav.registre.testnorge.synt.person.consumer.dto.SyntPersonDTO;

public class Person {
    private final SyntPersonDTO dto;
    private final String ident;

    public Person(SyntPersonDTO dto, String ident) {
        this.dto = dto;
        this.ident = ident;
    }

    public PersonDTO toDTO() {
        return PersonDTO
                .builder()
                .ident(ident)
                .fornavn(dto.getFornavn())
                .etternavn(dto.getSlektsnavn())
                .foedselsdato(dto.getConvertedFodselsdato())
                .adresse(AdresseDTO
                        .builder()
                        .gatenavn(dto.getAdressenavn())
                        .postnummer(dto.getPostnummer())
                        .kommunenummer(dto.getKommunenummer())
                        .build()
                )
                .build();
    }

}
