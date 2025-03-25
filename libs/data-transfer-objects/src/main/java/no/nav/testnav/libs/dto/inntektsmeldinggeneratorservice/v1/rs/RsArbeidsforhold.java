package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsArbeidsforhold {

    private String arbeidsforholdId;
    private String foersteFravaersdag;
    private RsInntekt beregnetInntekt;
    private List<RsPeriode> avtaltFerieListe;
    private List<RsUtsettelseAvForeldrepenger> utsettelseAvForeldrepengerListe;
    private List<RsGraderingIForeldrepenger> graderingIForeldrepengerListe;

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
