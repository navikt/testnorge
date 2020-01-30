package no.nav.registre.inntektsmeldingstub.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

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
    private String arbeidforholdsId;
    private LocalDate foersteFravaersdag;
    private double beloep;
    private String aarsakVedEndring;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "utsettelse_av_foreldrepenger_id", referencedColumnName = "id")
    private List<UtsettelseAvForeldrepenger> utsettelseAvForeldrepengerListe = Collections.emptyList();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "gradering_i_foreldrepenger_id", referencedColumnName = "id")
    private List<GraderingIForeldrepenger> graderingIForeldrepengerListe = Collections.emptyList();

}
