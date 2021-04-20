package no.nav.organisasjonforvalter.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlVegAdresse {

    private String matrikkelId;
    private String adressenavn;
    private Integer husnummer;
    private String husbokstav;
    private String postnummer;
    private String poststed;
    private String kommunenummer;
    private String kommunenavn;
    private String bydelsnummer;
    private String bydelsnavn;
    private String tilleggsnavn;
}
