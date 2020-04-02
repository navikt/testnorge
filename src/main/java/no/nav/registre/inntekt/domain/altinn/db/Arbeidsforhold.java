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
    private List<Periode> avtaltFerieListe;
    @JsonProperty
    private Long id;
    @JsonProperty
    private String arbeidforholdsId;
    @JsonProperty
    private LocalDate foersteFravaersdag;
    @JsonProperty
    private double beloep;
    @JsonProperty
    private String aarsakVedEndring;
    @JsonProperty
    private List<UtsettelseAvForeldrepenger> utsettelseAvForeldrepengerListe;
    @JsonProperty
    private List<GraderingIForeldrepenger> graderingIForeldrepengerListe;

}
