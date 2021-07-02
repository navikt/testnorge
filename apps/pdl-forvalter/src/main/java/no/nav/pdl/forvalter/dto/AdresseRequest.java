package no.nav.pdl.forvalter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdresseRequest {

    private String matrikkelId;

    private String adressenavn;
    private String husnummer;
    private String husbokstav;

    private String postnummer;
    private String kommunenummer;
    private String bydelsnummer;

    private String poststed;
    private String kommunenavn;
    private String bydelsnavn;
    private String tilleggsnavn;

    @Schema(description = "Fritekstsøk", example= "Sannergata 2 0557 Oslo")
    private String fritekst;
}
