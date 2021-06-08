package no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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

    public List<RsPeriode> getAvtaltFerieListe() {
        return Objects.requireNonNullElse(avtaltFerieListe, Collections.emptyList());
    }

    public List<RsUtsettelseAvForeldrepenger> getUtsettelseAvForeldrepengerListe() {
        return Objects.requireNonNullElse(utsettelseAvForeldrepengerListe, Collections.emptyList());
    }

    public List<RsGraderingIForeldrepenger> getGraderingIForeldrepengerListe() {
        return Objects.requireNonNullElse(graderingIForeldrepengerListe, Collections.emptyList());
    }


}
