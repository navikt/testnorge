package no.nav.testnav.libs.dto.aareg.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "permisjonPermitteringId")
@Schema(description = "Informasjon om permisjon eller permittering")
public class PermisjonPermittering {

    @Schema(description = "Id fra opplysningspliktig", example = "123-xyz")
    private String permisjonPermitteringId;

    @Schema(description = "Periode for permisjon eller permittering")
    private Periode periode;

    @Schema(description = "Prosent for permisjon eller permittering", example = "50.5")
    private Double prosent;

    @Schema(description = "Permisjon-/permitteringstype (kodeverk: PermisjonsOgPermitteringsBeskrivelse)", example = "permisjonMedForeldrepenger")
    private String type;

    @Schema(description = "Varslingskode (kodeverk: Varslingskode_Aa-registeret) - benyttes hvis permisjon/permittering er lukket maskinelt")
    private String varslingskode;
}
