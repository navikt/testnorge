package no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({ "permisjonPermitteringId", "periode", "prosent", "type", "sporingsinformasjon" })
@ApiModel(
        description = "Informasjon om permisjon eller permittering"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class PermisjonPermittering {

    @ApiModelProperty(
            notes = "Id fra opplysningspliktig",
            example = "123-xyz"
    )
    private String permisjonPermitteringId;
    @ApiModelProperty(
            notes = "Periode for permisjon eller permittering"
    )
    private Periode periode;
    @ApiModelProperty(
            notes = "Prosent for permisjon eller permittering",
            example = "50.5"
    )
    private Double prosent;
    @ApiModelProperty(
            notes = "Permisjon-/permitteringstype (kodeverk: PermisjonsOgPermitteringsBeskrivelse)",
            example = "permisjonMedForeldrepenger"
    )
    private String type;
    @ApiModelProperty(
            notes = "Varslingskode (kodeverk: Varslingskode_5fAa-registeret) - benyttes hvis permisjon/permittering er lukket maskinelt"
    )
    private String varslingskode;
    @ApiModelProperty(
            notes = "Informasjon om opprettelse og endring av objekt"
    )
    private Sporingsinformasjon sporingsinformasjon;
}
