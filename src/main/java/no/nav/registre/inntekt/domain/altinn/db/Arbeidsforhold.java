package no.nav.registre.inntekt.domain.altinn.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
@NoArgsConstructor(force = true)
public class Arbeidsforhold {
    @JsonProperty
    List<Periode> avtaltFerieListe;
    @JsonProperty
    Long id;
    @JsonProperty
    String arbeidforholdsId;
    @JsonProperty
    LocalDate foersteFravaersdag;
    @JsonProperty
    double beloep;
    @JsonProperty
    String aarsakVedEndring;
    @JsonProperty
    List<UtsettelseAvForeldrepenger> utsettelseAvForeldrepengerListe;
    @JsonProperty
    List<GraderingIForeldrepenger> graderingIForeldrepengerListe;

}
