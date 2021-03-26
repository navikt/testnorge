package no.nav.adresse.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdresseRequest {

    private String matrikkelId;

    private String veinavn;
    private String husnummer;
    private String husbokstav;

    private String postnummer;
    private String kommunenummer;
    private String bydelsnummer;

    private String poststed;
    private String kommunenavn;
    private String bydelsnavn;
    private String tilleggsnavn;
}
