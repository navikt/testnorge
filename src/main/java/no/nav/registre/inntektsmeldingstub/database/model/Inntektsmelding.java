package no.nav.registre.inntektsmeldingstub.database.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@Table(name = "inntektsmelding")
@AllArgsConstructor
@NoArgsConstructor
public class Inntektsmelding {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "opphoer_av_naturalytelse_id", referencedColumnName = "id")
    List<NaturalytelseDetaljer> opphoerAvNaturalytelseListe;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "gjenopptakelse_naturalytelse_id", referencedColumnName = "id")
    List<NaturalytelseDetaljer> gjenopptakelseNaturalytelseListe;
    @Id
    @GeneratedValue
    private Integer id;
    private String ytelse;
    private String aarsakTilInnsending;
    @ManyToOne
    @JoinColumn(name = "arbeidsgiver_id", referencedColumnName = "id")
    @JsonBackReference(value = "inntektsmeldinger")
    private Arbeidsgiver arbeidsgiver;
    private String arbeidstakerFnr;
    private boolean naerRelasjon;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "arbeidsforhold_id", referencedColumnName = "id")
    private Arbeidsforhold arbeidsforhold;
    private double refusjonsbeloepPrMnd;
    private LocalDate refusjonsopphoersdato;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "refusjon_endring_id", referencedColumnName = "id")
    private List<RefusjonsEndring> refusjonsEndringListe;
    private double sykepengerBruttoUtbetalt;
    private String sykepengerBegrunnelseForReduksjonEllerIkkeUtbetalt;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "sykepenger_periode_id", referencedColumnName = "id")
    private List<Periode> sykepengerPerioder;
    private LocalDate startdatoForeldrepengeperiode;
    private String avsendersystemNavn;
    private String avsendersystemVersjon;
    private LocalDateTime innsendingstidspunkt;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "pleiepenger_periode_id", referencedColumnName = "id")
    private List<Periode> pleiepengerPeriodeListe;


    private boolean omsorgHarUtbetaltPliktigeDager;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "omsorgspenger_fravaers_periode_id", referencedColumnName = "id")
    private List<Periode> omsorgspengerFravaersPeriodeListe;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "omsorgspenger_delvis_fravaers_id", referencedColumnName = "id")
    private List<DelvisFravaer> omsorgspengerDelvisFravaersListe;

}
