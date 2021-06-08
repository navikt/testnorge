package no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RsArbeidsforhold {

    @JsonProperty
    private String arbeidsforholdId;
    @JsonProperty
    private LocalDate foersteFravaersdag;
    @JsonProperty
    private RsInntekt beregnetInntekt;
    @JsonProperty
    private List<RsPeriode> avtaltFerieListe;
    @JsonProperty
    private List<RsUtsettelseAvForeldrepenger> utsettelseAvForeldrepengerListe;
    @JsonProperty
    private List<RsGraderingIForeldrepenger> graderingIForeldrepengerListe;

    public Optional<String> getArbeidsforholdId() {
        return Optional.ofNullable(arbeidsforholdId);
    }

    public Optional<LocalDate> getFoersteFravaersdag() {
        return Optional.ofNullable(foersteFravaersdag);
    }

    public Optional<RsInntekt> getBeregnetInntekt() {
        return Optional.ofNullable(beregnetInntekt);
    }

    public Optional<List<RsPeriode>> getAvtaltFerieListe() {
        return Optional.ofNullable(avtaltFerieListe);
    }

    public Optional<List<RsUtsettelseAvForeldrepenger>> getUtsettelseAvForeldrepengerListe() {
        return Optional.ofNullable(utsettelseAvForeldrepengerListe);
    }

    public Optional<List<RsGraderingIForeldrepenger>> getGraderingIForeldrepengerListe() {
        return Optional.ofNullable(graderingIForeldrepengerListe);
    }
}
