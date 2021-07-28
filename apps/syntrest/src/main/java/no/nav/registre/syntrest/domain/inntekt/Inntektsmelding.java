package no.nav.registre.syntrest.domain.inntekt;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Inntektsmelding {

    @JsonProperty
    private int aar;
    @JsonProperty
    private double beloep;
    @JsonProperty
    private String fnr;
    @JsonProperty
    private boolean inngaarIGrunnleggForTrekk;
    @JsonProperty
    @ApiModelProperty(example = "INNTEKT")
    private String inntektsinformasjonsType;
    @JsonProperty
    @ApiModelProperty(example = "Loennsinntekt, YtelseFraOffentlige")
    private String inntektstype;
    @JsonProperty
    @ApiModelProperty(value = "Navn på måned", example = "november")
    private String maaned;
    @JsonProperty
    private boolean utloeserArbeidsgiveravgift;
    @JsonProperty
    @ApiModelProperty(value = "Virksomhetsnummer/OrgansiasjonsID")
    private String virksomhet;
}
