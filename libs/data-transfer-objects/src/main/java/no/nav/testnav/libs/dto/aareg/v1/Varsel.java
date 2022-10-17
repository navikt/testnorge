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
@Schema(description = "Informasjon om varsel")
public class Varsel {

    @Schema(description = "Entitet for varsel")
    private Varselentitet entitet;

    @Schema(description = "Varslingskode (kodeverk: Varslingskode_5fAa-registeret)")
    private String varslingskode;
}
