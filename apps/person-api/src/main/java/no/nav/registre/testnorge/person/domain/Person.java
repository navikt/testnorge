package no.nav.registre.testnorge.person.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

import no.nav.registre.testnorge.dto.person.v1.PersonDTO;
import no.nav.registre.testnorge.person.consumer.dto.graphql.Bostedsadresse;
import no.nav.registre.testnorge.person.consumer.dto.graphql.Folkeregisteridentifikator;
import no.nav.registre.testnorge.person.consumer.dto.graphql.HentPerson;
import no.nav.registre.testnorge.person.consumer.dto.graphql.Navn;
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
        Optional<Navn> navn = person.getNavn().stream().findFirst();
        Optional<Bostedsadresse> bostedsadresse = person.getBostedsadresse().stream().findFirst();
        Optional<Folkeregisteridentifikator> folkeregisteridentifikator = person.getFolkeregisteridentifikator().stream().findFirst();

        ident = folkeregisteridentifikator.isEmpty() ? null : folkeregisteridentifikator.get().getIdentifikasjonsnummer();
        fornavn = navn.isEmpty() ? null : navn.get().getFornavn();
        mellomnavn = navn.isEmpty() ? null : navn.get().getMellomnavn();
        etternavn = navn.isEmpty() ? null : navn.get().getEtternavn();
        if (!bostedsadresse.isEmpty()) {
            if (bostedsadresse.get().getVegadresse() != null) {
                adresse = new Adresse(bostedsadresse.get().getVegadresse());
            }
        }
    }

    public PersonDTO toDTO() {
        return PersonDTO.builder()
                .ident(ident)
                .fornavn(fornavn)
                .mellomnavn(mellomnavn)
                .etternavn(etternavn)
                .adresse(adresse != null ? adresse.toDto() : null)
                .build();
    }
}
