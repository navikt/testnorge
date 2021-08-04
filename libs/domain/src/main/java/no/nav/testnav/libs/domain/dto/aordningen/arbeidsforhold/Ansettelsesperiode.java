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
@JsonPropertyOrder({ "periode", "varslingskode", "bruksperiode", "sporingsinformasjon" })
@ApiModel(
        description = "Informasjon knyttet til ansettelsesperioden"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Ansettelsesperiode {

    @ApiModelProperty(
            notes = "Ansettelsesperiode"
    )
    private Periode periode;
    @ApiModelProperty(
            notes = "Varslingskode (kodeverk: Varslingskode_5fAa-registeret) - benyttes hvis ansettelsesperiode er lukket maskinelt",
            example = "ERKONK"
    )
    private String varslingskode;
    @ApiModelProperty(
            notes = "Bruksperiode for ansettelsesperiode"
    )
    private Bruksperiode bruksperiode;
    @ApiModelProperty(
            notes = "Informasjon om opprettelse og endring av objekt"
    )
    private Sporingsinformasjon sporingsinformasjon;
}
