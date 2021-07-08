package no.nav.testnav.libs.dto.personservice.v1;

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
    String fornavn;
    String mellomnavn;
    String etternavn;
    LocalDate foedselsdato;
    AdresseDTO adresse;
    String postnr;
    String by;
    String opprinnelse;
    Set<String> tags;
}
