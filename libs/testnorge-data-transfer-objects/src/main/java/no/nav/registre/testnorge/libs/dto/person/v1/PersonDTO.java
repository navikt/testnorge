package no.nav.registre.testnorge.libs.dto.person.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PersonDTO {

    @JsonProperty(required = true)
    String ident;
    @JsonProperty
    LocalDate foedselsdato;
    @JsonProperty(required = true)
    String fornavn;
    @JsonProperty
    String mellomnavn;
    @JsonProperty(required = true)
    String etternavn;
    @JsonProperty
    AdresseDTO adresse;

}
