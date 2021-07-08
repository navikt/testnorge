package no.nav.testnav.dto.personfastedataservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Set;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PersonDTO {
    String ident;
    String fornavn;
    String etternavn;
    String adresse;
    String postnr;
    String by;
    String opprinnelse;
    Set<String> tags;
}
