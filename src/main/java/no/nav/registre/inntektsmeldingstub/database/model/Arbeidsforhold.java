package no.nav.registre.inntektsmeldingstub.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;

@Entity
@Table(name = "arbeidsforhold")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Arbeidsforhold {

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "avtalt_ferie_periode_id", referencedColumnName = "id")
    List<Periode> avtaltFerieListe = Collections.emptyList();
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "arbeidsforholds_id")
    private String arbeidforholdsId;
    @Column(name = "foerste_fravaersdag")
    private LocalDate foersteFravaersdag;
    private double beloep;
    @Column(name = "aarsak_ved_endring")
    private String aarsakVedEndring;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "utsettelse_av_foreldrepenger_id", referencedColumnName = "id")
    private List<UtsettelseAvForeldrepenger> utsettelseAvForeldrepengerListe = Collections.emptyList();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "gradering_i_foreldrepenger_id", referencedColumnName = "id")
    private List<GraderingIForeldrepenger> graderingIForeldrepengerListe = Collections.emptyList();


    public List<GraderingIForeldrepenger> getGraderingIForeldrepengerListe() {
        if (isNull(graderingIForeldrepengerListe)) { graderingIForeldrepengerListe = new ArrayList<>(); }
        return graderingIForeldrepengerListe;
    }

    public List<UtsettelseAvForeldrepenger> getUtsettelseAvForeldrepenger() {
        if (isNull(utsettelseAvForeldrepengerListe)) { return new ArrayList<>(); }
        return utsettelseAvForeldrepengerListe;
    }

}
