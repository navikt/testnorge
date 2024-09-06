package no.nav.testnav.libs.dto.levendearbeidsforhold.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "permisjonPermitteringId",
        "periode",
        "prosent",
        "type",
        "sporingsinformasjon"
})
@Schema(description = "Informasjon om permisjon eller permittering")
public class PermisjonPermittering {

    @Schema(description = "Id fra opplysningspliktig", example = "123-xyz")
    private String permisjonPermitteringId;

    private Periode periode;

    @Schema(description = "Prosent for permisjon eller permittering", example = "50.5")
    private Double prosent;

    @Schema(description = "Permisjon-/permitteringstype (kodeverk: PermisjonsOgPermitteringsBeskrivelse)", example = "permisjonMedForeldrepenger")
    private String type;

    @Schema(description = "Varslingskode (kodeverk: Varslingskode_5fAa-registeret) - benyttes hvis permisjon/permittering er lukket maskinelt")
    private String varslingskode;

    @Schema(description = "Informasjon om opprettelse og endring av objekt")
    private Sporingsinformasjon sporingsinformasjon;
}
