package no.nav.testnav.libs.dto.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AdresseDTO {

    String gatenavn;
    String postnummer;
    String poststed;
    String kommunenummer;
}
