package no.nav.registre.testnorge.originalpopulasjon.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

import no.nav.registre.testnorge.libs.dto.person.v1.PersonDTO;

@Value
@Builder
@AllArgsConstructor
public class Person {

    String ident;
    LocalDate foedselsdato;
    String fornavn;
    String mellomnavn;
    String etternavn;
    Adresse adresse;

    public Person(PersonDTO dto) {
        ident = dto.getIdent();
        foedselsdato = dto.getFoedselsdato();
        fornavn = dto.getFornavn();
        mellomnavn = dto.getMellomnavn();
        etternavn = dto.getEtternavn();
        adresse = dto.getAdresse() != null ? new Adresse(dto.getAdresse()) : null;
    }

    public PersonDTO toDTO() {
        return PersonDTO.builder()
                .ident(ident)
                .foedselsdato(foedselsdato)
                .fornavn(fornavn)
                .mellomnavn(mellomnavn)
                .etternavn(etternavn)
                .adresse(adresse != null ? adresse.toDto() : null)
                .build();
    }
}
