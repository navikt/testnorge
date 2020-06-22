package no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.rs.RsGraderingIForeldrepenger;
import no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.rs.RsPeriode;

@ApiModel
@Builder
@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class RsAltinnArbeidsforhold {

    @JsonProperty(defaultValue = "Hentet fra Aareg")
    @ApiModelProperty(value = "Unik ID for det gjeldende arbeidsforholdet")
    private String arbeidsforholdId;
    @JsonProperty
    @ApiModelProperty(value = "Første fraværsdag", example = "YYYY-MM-DD")
    private LocalDate foersteFravaersdag;
    @JsonProperty
    @ApiModelProperty(value = "Beregnet inntekt", required = true)
    private RsAltinnInntekt beregnetInntekt;
    @JsonProperty
    @ApiModelProperty(value = "Ferieliste bestående av perioder med ferie")
    private List<RsPeriode> avtaltFerieListe;
    @JsonProperty
    @ApiModelProperty
    private List<RsAltinnUtsettelseAvForeldrepenger> utsettelseAvForeldrepengerListe;
    @JsonProperty
    @ApiModelProperty
    private List<RsGraderingIForeldrepenger> graderingIForeldrepengerListe;


    public List<RsPeriode> getAvtaltFerieListe() {
        return Objects.requireNonNullElse(avtaltFerieListe, Collections.emptyList());
    }

    public List<RsAltinnUtsettelseAvForeldrepenger> getUtsettelseAvForeldrepengerListe() {
        return Objects.requireNonNullElse(utsettelseAvForeldrepengerListe, Collections.emptyList());
    }

    public List<RsGraderingIForeldrepenger> getGraderingIForeldrepengerListe() {
        return Objects.requireNonNullElse(graderingIForeldrepengerListe, Collections.emptyList());
    }
}
