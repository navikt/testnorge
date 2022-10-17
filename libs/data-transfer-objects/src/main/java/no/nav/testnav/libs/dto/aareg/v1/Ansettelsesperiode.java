package no.nav.testnav.libs.dto.aareg.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Informasjon knyttet til ansettelsesperioden")
public class Ansettelsesperiode {

    @Schema(description = "Ansettelsesperiode")
    private Periode periode;

    @Schema(description = "&Aring;rsak for avsluttet ansettelsesperiode (kodeverk: Slutt%C3%A5rsakAareg)", example = "arbeidstakerHarSagtOppSelv")
    private String sluttaarsak;

    @Schema(description = "Varslingskode (kodeverk: Varslingskode_5fAa-registeret) - benyttes hvis ansettelsesperiode er lukket maskinelt", example = "ERKONK")
    private String varslingskode;

    @Schema(description = "Bruksperiode for ansettelsesperiode")
    private Bruksperiode bruksperiode;
}
