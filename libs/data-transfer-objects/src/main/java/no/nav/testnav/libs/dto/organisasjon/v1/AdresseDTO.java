package no.nav.testnav.libs.dto.organisasjon.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdresseDTO {
    private String kommunenummer;
    private String adresselinje1;
    private String adresselinje2;
    private String adresselinje3;
    private String landkode;
    private String postnummer;
    private String poststed;
}
