package no.nav.registre.testnorge.opprettpersonpdl.domain;

import lombok.Value;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.testnav.libs.dto.person.v1.PersonDTO;

@Value
public class Person {

    public Person(no.nav.testnav.libs.avro.person.Person person) {
        ident = person.getIdent().toString();
        foedselsdato = LocalDate.parse(person.getFoedselsdato());
        fornavn = person.getFornavn().toString();
        mellomnavn = person.getMellomnavn() != null ? person.getMellomnavn().toString() : null;
        etternavn = person.getEtternavn().toString();
        tags = person.getTags()
                .stream()
                .map(CharSequence::toString)
                .collect(Collectors.toList());
        adresse = person.getAdresse() != null ? new Adresse(person.getAdresse()) : null;
    }

    String ident;
    LocalDate foedselsdato;
    String fornavn;
    String mellomnavn;
    String etternavn;
    List<String> tags;
    Adresse adresse;

    public PersonDTO toDTO() {
        return PersonDTO
                .builder()
                .ident(ident)
                .foedselsdato(foedselsdato)
                .fornavn(fornavn)
                .mellomnavn(mellomnavn)
                .etternavn(etternavn)
                .adresse(adresse != null ? adresse.toDTO() : null)
                .tags(new HashSet<>(tags))
                .build();
    }

    public String toKommaseparerteTags() {
        return String.join(",", tags);
    }

}
