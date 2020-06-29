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
import no.nav.registre.testnorge.person.consumer.dto.graphql.Vegadresse;

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

        ident = person.getFolkeregisteridentifikator()
                .stream()
                .findFirst()
                .map(Folkeregisteridentifikator::getIdentifikasjonsnummer)
                .orElse(null);
        fornavn = navn.map(Navn::getFornavn).orElse(null);
        mellomnavn = navn.map(Navn::getMellomnavn).orElse(null);
        etternavn = navn.map(Navn::getEtternavn).orElse(null);
        adresse = person.getBostedsadresse()
                .stream()
                .findFirst()
                .map(value -> new Adresse(value.getVegadresse()))
                .orElse(null);
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
