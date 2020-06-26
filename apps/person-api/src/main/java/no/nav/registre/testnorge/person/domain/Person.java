package no.nav.registre.testnorge.person.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import no.nav.registre.testnorge.dto.person.v1.PersonDTO;
import no.nav.registre.testnorge.person.consumer.dto.graphql.HentPerson;
import no.nav.registre.testnorge.person.consumer.dto.graphql.PdlPerson;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    private String ident;
    private String fornavn;
    private String mellomnavn;
    private String etternavn;
    private Adresse adresse;

    public Person(PersonDTO dto) {
        Person.builder()
                .ident(dto.getIdent())
                .fornavn(dto.getFornavn())
                .mellomnavn(dto.getMellomnavn())
                .etternavn(dto.getEtternavn())
                .adresse(dto.getAdresse() != null ? new Adresse(dto.getAdresse()) : null)
                .build();
    }

    public Person(PdlPerson pdlPerson) {
        HentPerson person = pdlPerson.getData().getHentPerson();

        Person.builder()
                .ident(person.getFolkeregisteridentifikator().getIdentifikasjonsnummer())
                .fornavn(person.getNavn().getFornavn())
                .mellomnavn(person.getNavn().getMellomnavn())
                .etternavn(person.getNavn().getEtternavn())
                .adresse(person.getBostedsadresse().getVegadresse() != null
                        ? new Adresse(person.getBostedsadresse().getVegadresse())
                        : null)
                .build();
    }

    public PersonDTO toDTO() {
        return PersonDTO.builder()
                .ident(ident)
                .fornavn(fornavn)
                .mellomnavn(mellomnavn)
                .etternavn(etternavn)
                .adresse(adresse.toDto())
                .build();
    }
}
