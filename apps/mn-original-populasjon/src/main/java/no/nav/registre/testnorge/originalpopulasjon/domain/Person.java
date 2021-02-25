package no.nav.registre.testnorge.originalpopulasjon.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDate;
import java.util.Set;

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
    Set<String> tags;

    public PersonDTO toDTO() {
        return PersonDTO.builder()
                .ident(ident)
                .foedselsdato(foedselsdato)
                .fornavn(fornavn)
                .mellomnavn(Strings.isBlank(mellomnavn) ? null : mellomnavn)
                .etternavn(etternavn)
                .adresse(adresse != null ? adresse.toDto() : null)
                .tags(tags)
                .build();
    }
}
