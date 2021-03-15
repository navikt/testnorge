package no.nav.registre.testnorge.libs.dto.bridge.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AdresseDTO {
    String postnummer;
    String landkode;
    String kommunenummer;
    String poststed;
    String postadresse1;
    String postadresse2;
    String postadresse3;
    String linjenummer;
    String vegadresseId;
}
