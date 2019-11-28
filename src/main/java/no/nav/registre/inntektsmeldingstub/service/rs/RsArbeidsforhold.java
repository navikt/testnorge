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
import java.util.Optional;

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
    private List<RsGraderingIForeldrepenger> graderingIForeldrepengerListe;

    public Optional<String> getArbeidsforholdId() { return Optional.ofNullable(arbeidsforholdId); }
    public Optional<LocalDate> getFoersteFravaersdag() { return Optional.ofNullable(foersteFravaersdag); }
    public Optional<RsInntekt> getBeregnetInntekt() { return Optional.ofNullable(beregnetInntekt); }
    public Optional<List<RsPeriode>> getAvtaltFerieListe() { return Optional.ofNullable(avtaltFerieListe); }
    public Optional<List<RsUtsettelseAvForeldrepenger>> getUtsettelseAvForeldrepengerListe() { return Optional.ofNullable(utsettelseAvForeldrepengerListe); }
    public Optional<List<RsGraderingIForeldrepenger>> getGraderingIForeldrepengerListe() { return Optional.ofNullable(graderingIForeldrepengerListe); }
}
