package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@ApiModel
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RsArbeidsforhold {

    @JsonProperty
    @ApiModelProperty("Unik ID for det gjeldende arbeidsforholdet")
    private String arbeidsforholdId;
    @JsonProperty
    @ApiModelProperty(value = "Første fraværsdag", example = "YYYY-MM-DD")
    private LocalDate foersteFravaersdag;
    @JsonProperty
    @ApiModelProperty("Beregnet inntekt")
    private RsInntekt beregnetInntekt;
    @JsonProperty
    @ApiModelProperty("Ferieliste bestående av perioder med ferie")
    private List<RsPeriode> avtaltFerieListe;
    @JsonProperty
    @ApiModelProperty()
    private List<RsUtsettelseAvForeldrepenger> utsettelseAvForeldrepengerListe;
    @JsonProperty
    @ApiModelProperty()
    private List<RsGraderingAvForeldrepenger> graderingIForeldrepengerListe;

}
