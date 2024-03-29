package no.nav.registre.sdforvalter.domain.person;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.nav.testnav.libs.dto.person.v1.PersonDTO;


@Value
@NoArgsConstructor(force = true)
public class Person {
    @JsonProperty
    String fnr;
    @JsonProperty("fornavn")
    String firstName;
    @JsonProperty("etternavn")
    String lastName;
    @JsonProperty("adresse")
    String address;
    @JsonProperty("postnr")
    String postNr;
    @JsonProperty("by")
    String city;

    public Person(PersonDTO dto) {
        fnr = dto.getIdent();
        firstName = dto.getFornavn();
        lastName = dto.getEtternavn();
        address = dto.getAdresse() != null ? dto.getAdresse().getGatenavn() : null;
        postNr = dto.getAdresse() != null ? dto.getAdresse().getPostnummer() : null;
        city = dto.getAdresse() != null ? dto.getAdresse().getPoststed() : null;
    }
}
