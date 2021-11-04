package no.nav.testnav.apps.tpsmessagingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class VegadresseRequest {

    private String matrikkelId;

    private String adressenavn;
    private String husnummer;
    private String husbokstav;

    private String postnummer;
    private String poststed;

    private String kommunenavn;
    private String kommunenummer;

    private String bydelsnummer;
    private String bydelsnavn;

    private String tilleggsnavn;

    @Schema(description = "Fritekstsøk", example= "Sannergata 2 0557 Oslo")
    private String fritekst;
}
