package no.nav.registre.inntektsmeldingstub.database.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Entity
@Getter
@Setter
@Builder
@Table(name = "inntektsmelding")
@AllArgsConstructor
@NoArgsConstructor
public class Inntektsmelding {

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "opphoer_av_naturalytelse_id", referencedColumnName = "id")
    private List<NaturalytelseDetaljer> opphoerAvNaturalytelseListe;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "gjenopptakelse_naturalytelse_id", referencedColumnName = "id")
    private List<NaturalytelseDetaljer> gjenopptakelseNaturalytelseListe;
    @Id
    @GeneratedValue
    private Long id;
    private String ytelse;
    private String aarsakTilInnsending;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arbeidsgiver_id", referencedColumnName = "id")
    private Arbeidsgiver arbeidsgiver;

    private String arbeidstakerFnr;
    private Boolean naerRelasjon;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "arbeidsforhold_id", referencedColumnName = "id")
    private Arbeidsforhold arbeidsforhold;
    private Double refusjonsbeloepPrMnd;
    private LocalDate refusjonsopphoersdato;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "refusjon_endring_id", referencedColumnName = "id")
    private List<EndringIRefusjon> endringIRefusjonListe;
    private Double sykepengerBruttoUtbetalt;
    private String sykepengerBegrunnelseForReduksjonEllerIkkeUtbetalt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "sykepenger_periode_id", referencedColumnName = "id")
    private List<Periode> sykepengerPerioder;
    private LocalDate startdatoForeldrepengeperiode;
    private String avsendersystemNavn;
    private String avsendersystemVersjon;
    private LocalDateTime innsendingstidspunkt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "pleiepenger_periode_id", referencedColumnName = "id")
    private List<Periode> pleiepengerPeriodeListe;


    private Boolean omsorgHarUtbetaltPliktigeDager;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "omsorgspenger_fravaers_periode_id", referencedColumnName = "id")
    private List<Periode> omsorgspengerFravaersPeriodeListe = Collections.emptyList();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "omsorgspenger_delvis_fravaers_id", referencedColumnName = "id")
    private List<DelvisFravaer> omsorgspengerDelvisFravaersListe = Collections.emptyList();

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eier_id", referencedColumnName = "id")
    private Eier eier;

    public Optional<Arbeidsgiver> getArbeidsgiver() {
        if (!Objects.isNull(arbeidsgiver) && arbeidsgiver.getVirksomhetsnummer().length() > 9) {
            return Optional.empty();
        }
        return Optional.ofNullable(arbeidsgiver);
    }
    public Optional<Arbeidsgiver> getPrivatArbeidsgiver() {
        if (!Objects.isNull(arbeidsgiver) && arbeidsgiver.getVirksomhetsnummer().length() == 11) {
            return Optional.of(arbeidsgiver);
        }
        return Optional.empty();
    }

    public List<NaturalytelseDetaljer> getOpphoerAvNaturalytelseListe() {
        if (Objects.isNull(opphoerAvNaturalytelseListe)) { return new ArrayList<>(); }
        return opphoerAvNaturalytelseListe;
    }

    public List<NaturalytelseDetaljer> getGjenopptakelseNaturalytelseListe() {
        if (Objects.isNull(gjenopptakelseNaturalytelseListe)) { return new ArrayList<>(); }
        return gjenopptakelseNaturalytelseListe;
    }

    public List<EndringIRefusjon> getEndringIRefusjonListe() {
        if (Objects.isNull(endringIRefusjonListe)) { return new ArrayList<>(); }
        return endringIRefusjonListe;
    }

    public List<Periode> getSykepengerPerioder() {
        if (Objects.isNull(sykepengerPerioder)) { return new ArrayList<>(); }
        return sykepengerPerioder;
    }

    public List<Periode> getPleiepengerPeriodeListe() {
        if (Objects.isNull(pleiepengerPeriodeListe)) { return new ArrayList<>(); }
        return pleiepengerPeriodeListe;
    }

    public List<Periode> getOmsorgspengerFravaersPeriodeListe() {
        if (Objects.isNull(pleiepengerPeriodeListe)) { return new ArrayList<>(); }
        return pleiepengerPeriodeListe;
    }
}
