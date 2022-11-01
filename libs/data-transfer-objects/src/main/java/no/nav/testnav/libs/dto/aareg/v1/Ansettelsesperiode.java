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

    @Schema(description = "Årsak for avsluttet ansettelsesperiode (kodeverk: SluttÅrsakAareg)", example = "arbeidstakerHarSagtOppSelv")
    private String sluttaarsak;

    @Schema(description = "Varslingskode (kodeverk: Varslingskode_Aa-registeret) - benyttes hvis ansettelsesperiode er lukket maskinelt", example = "ERKONK")
    private String varslingskode;

    @Schema(description = "Bruksperiode for ansettelsesperiode")
    private Bruksperiode bruksperiode;
}
