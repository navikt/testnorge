package no.nav.testnav.libs.data.pdlforvalter.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VegadresseDTO implements Serializable {

    private String adressekode;
    private String adressenavn;
    private String tilleggsnavn;
    private String bruksenhetsnummer;
    private String husbokstav;
    private String husnummer;
    private String kommunenummer;
    private String bydelsnummer;
    private String postnummer;
}