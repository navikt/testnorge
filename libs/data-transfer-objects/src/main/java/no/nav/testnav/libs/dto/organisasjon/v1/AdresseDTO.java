package no.nav.testnav.libs.dto.organisasjon.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AdresseDTO {
    String kommunenummer;
    String adresselinje1;
    String adresselinje2;
    String adresselinje3;
    String landkode;
    String postnummer;
    String poststed;
}
