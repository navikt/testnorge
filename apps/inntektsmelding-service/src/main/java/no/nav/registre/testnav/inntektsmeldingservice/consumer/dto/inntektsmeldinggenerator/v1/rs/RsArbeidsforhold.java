package no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Builder
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class RsArbeidsforhold {

    @JsonProperty(defaultValue = "Hentet fra Aareg")
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
