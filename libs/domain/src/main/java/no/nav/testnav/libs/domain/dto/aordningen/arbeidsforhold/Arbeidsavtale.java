package no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({ "arbeidstidsordning", "yrke", "stillingsprosent", "antallTimerPrUke", "beregnetAntallTimerPrUke", "sistLoennsendring", "sistStillingsendring", "bruksperiode", "gyldighetsperiode",
        "sporingsinformasjon" })
@ApiModel(
        description = "Informasjon om arbeidsavtalen"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Arbeidsavtale {

    @ApiModelProperty(
            notes = "Arbeidstidsordning (kodeverk: Arbeidstidsordninger)",
            example = "ikkeSkift"
    )
    private String arbeidstidsordning;
    @ApiModelProperty(
            notes = "Yrke (kodeverk: Yrker)",
            example = "2130123"
    )
    private String yrke;
    @ApiModelProperty(
            notes = "Stillingsprosent",
            example = "49.5"
    )
    private Double stillingsprosent;
    @ApiModelProperty(
            notes = "Antall timer per uke",
            example = "37.5"
    )
    private Fartoey fartoey;
    @ApiModelProperty(
            notes = "Fartøy i maritimt arbeidsforhold"
    )
    private Double antallTimerPrUke;
    @ApiModelProperty(
            notes = "Beregnet antall timer per uke",
            example = "37.5"
    )
    private Double beregnetAntallTimerPrUke;
    @ApiModelProperty(
            notes = "Dato for siste l&oslash;nnsendring, format (ISO-8601): yyyy-MM-dd",
            example = "2014-07-15"
    )
    private LocalDate sisteLoennsendringsdato;
    @ApiModelProperty(
            notes = "Dato for siste stillingsendring, format (ISO-8601): yyyy-MM-dd",
            example = "2015-12-15"
    )
    private LocalDate sistStillingsendring;
    @ApiModelProperty(
            notes = "Bruksperiode for arbeidsavtalen"
    )
    private Bruksperiode bruksperiode;
    @ApiModelProperty(
            notes = "Gyldighetsperiode for arbeidsavtalen"
    )
    private Gyldighetsperiode gyldighetsperiode;
    @ApiModelProperty(
            notes = "Informasjon om opprettelse og endring av objekt"
    )
    private Sporingsinformasjon sporingsinformasjon;

    @JsonProperty("sistLoennsendring")
    public String getSistLoennsendringAsString() {
        return JavaTimeUtil.toString(this.sisteLoennsendringsdato);
    }

    @JsonProperty("sistStillingsendring")
    public String getSistStillingsendringAsString() {
        return JavaTimeUtil.toString(this.sistStillingsendring);
    }
}
