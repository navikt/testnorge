package no.nav.testnav.dto.personservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.Set;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PersonDTO {

    String ident;
    LocalDate foedselsdato;
    String fornavn;
    String mellomnavn;
    String etternavn;
    AdresseDTO adresse;
    Set<String> tags;
}
