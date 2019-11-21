package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@ApiModel
@Builder
@NoArgsConstructor
public class Arbeidsforhold {

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
    private List<Periode> avtaltFerieListe;
    @JsonProperty
    @ApiModelProperty()
    private List<UtsettelseAvForeldrepenger> utsettelseAvForeldrepengerListe;
    @JsonProperty
    @ApiModelProperty()
    private List<GraderingAvForeldrepenger> graderingIForeldrepengerListe;

}
