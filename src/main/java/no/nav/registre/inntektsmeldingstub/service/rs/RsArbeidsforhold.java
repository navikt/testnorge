package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@ApiModel
@Builder
@Getter
@NoArgsConstructor
public class RsArbeidsforhold {

    @JsonProperty
    @ApiModelProperty()
    private String arbeidsforholdId;
    @JsonProperty
    @ApiModelProperty()
    private Date foersteFravaersdag;
    @JsonProperty
    @ApiModelProperty()
    private double beregnetInntekt;
    @JsonProperty
    @ApiModelProperty()
    private List<RsPeriode> avtaltFerieListe;
    @JsonProperty
    @ApiModelProperty()
    private List<RsUtsettelseAvForeldrepenger> utsettelseAvForeldrepengerListe;
    @JsonProperty
    @ApiModelProperty()
    private List<RsGraderingAvForeldrepenger> graderingIForeldrepengerListe;

}
