package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApiModel
@Builder
@Getter
public class RsInntektsmelding {

    @JsonProperty
    @ApiModelProperty(required = true)
    private String ytelse;
    @JsonProperty
    @ApiModelProperty(required = true)
    private String aarsakTilInnsending;
    @JsonProperty
    @Size(min = 11, max = 11)
    @ApiModelProperty(value = "Arbeidstakers fødselsnummer", required = true)
    private String arbeidstakerFnr;
    @JsonProperty
    @ApiModelProperty(value = "default = \'false\'", required = true)
    private boolean naerRelasjon;
    @JsonProperty
    @ApiModelProperty(required = true)
    private RsAvsendersystem avsendersystem;

    @JsonProperty
    @ApiModelProperty("For inntektsmeldingstype 201812 må enten arbeidsgiver eller arbeidsgiverPrivat være satt." +
            "For inntektsmeldingstype 201809 må arbeidsgiver være satt.")
    private RsArbeidsgiver arbeidsgiver;
    @JsonProperty
    @ApiModelProperty("For inntektsmeldingstype 201812 må enten arbeidsgiver eller arbeidsgiverPrivat være satt." +
            "For inntektsmeldingstype 201809 må arbeidsgiver være satt.")
    private RsArbeidsgiverPrivat arbeidsgiverPrivat;

    @JsonProperty
    @ApiModelProperty(value = "Gjeldende arbeidsforhold. \'nillable\' i XSD skjema.", required = true)
    private RsArbeidsforhold arbeidsforhold;

    @JsonProperty
    @ApiModelProperty()
    private RsRefusjon refusjon;
    @JsonProperty
    @ApiModelProperty()
    private RsOmsorgspenger omsorgspenger;
    @JsonProperty
    @ApiModelProperty()
    private RsSykepengerIArbeidsgiverperioden sykepengerIArbeidsgiverperioden;
    @JsonProperty
    @ApiModelProperty(example = "YYYY-MM-DD")
    private LocalDate startdatoForeldrepengeperiode;
    @JsonProperty
    @ApiModelProperty()
    private List<RsNaturaYtelseDetaljer> opphoerAvNaturalytelseListe;
    @JsonProperty
    @ApiModelProperty()
    private List<RsNaturaYtelseDetaljer> gjenopptakelseNaturalytelseListe;
    @JsonProperty
    @ApiModelProperty()
    private List<RsPeriode> pleiepengerPerioder;

    @JsonCreator
    public RsInntektsmelding(String ytelse, String aarsakTilInnsending, @Size(min = 11, max = 11) String arbeidstakerFnr, boolean naerRelasjon,
            RsAvsendersystem avsendersystem, RsArbeidsgiver arbeidsgiver, RsArbeidsgiverPrivat arbeidsgiverPrivat, RsArbeidsforhold arbeidsforhold, RsRefusjon refusjon,
            RsOmsorgspenger omsorgspenger, RsSykepengerIArbeidsgiverperioden sykepengerIArbeidsgiverperioden, LocalDate startdatoForeldrepengeperiode,
            List<RsNaturaYtelseDetaljer> opphoerAvNaturalytelseListe, List<RsNaturaYtelseDetaljer> gjenopptakelseNaturalytelseListe,
            List<RsPeriode> pleiepengerPerioder) {
        log.info("Bruker egendefinert konstruktør for RsInntektsmelding objektet.");
        this.ytelse = ytelse;
        this.aarsakTilInnsending = aarsakTilInnsending;
        this.arbeidstakerFnr = arbeidstakerFnr;
        this.naerRelasjon = naerRelasjon;
        this.avsendersystem = avsendersystem;
        this.arbeidsgiver = arbeidsgiver;
        this.arbeidsgiverPrivat = arbeidsgiverPrivat;
        this.arbeidsforhold = arbeidsforhold;
        this.refusjon = refusjon;
        this.omsorgspenger = omsorgspenger;
        this.sykepengerIArbeidsgiverperioden = sykepengerIArbeidsgiverperioden;
        this.startdatoForeldrepengeperiode = startdatoForeldrepengeperiode;
        this.opphoerAvNaturalytelseListe = opphoerAvNaturalytelseListe;
        this.gjenopptakelseNaturalytelseListe = gjenopptakelseNaturalytelseListe;
        this.pleiepengerPerioder = pleiepengerPerioder;
    }

    @JsonIgnore
    public Optional<RsArbeidsgiver> getArbeidsgiver() { return Optional.ofNullable(arbeidsgiver); }
    @JsonIgnore
    public Optional<RsArbeidsgiverPrivat> getArbeidsgiverPrivat() { return Optional.ofNullable(arbeidsgiverPrivat); }
    @JsonIgnore
    public Optional<RsRefusjon> getRefusjon() { return Optional.ofNullable(refusjon); }
    @JsonIgnore
    public Optional<RsOmsorgspenger> getOmsorgspenger() { return Optional.ofNullable(omsorgspenger); }
    @JsonIgnore
    public Optional<RsSykepengerIArbeidsgiverperioden> getSykepengerIArbeidsgiverPerioden() { return Optional.ofNullable(sykepengerIArbeidsgiverperioden); }
    @JsonIgnore
    public Optional<LocalDate> getStartdatoForeldrepengeperiode() { return Optional.ofNullable(startdatoForeldrepengeperiode); }
    @JsonIgnore
    public Optional<List<RsNaturaYtelseDetaljer>> getOpphoerAvNaturalytelseListe() { return Optional.ofNullable(opphoerAvNaturalytelseListe); }
    @JsonIgnore
    public Optional<List<RsNaturaYtelseDetaljer>> getGjenopptakelseNaturalytelseListe() { return Optional.ofNullable(gjenopptakelseNaturalytelseListe); }
    @JsonIgnore
    public Optional<List<RsPeriode>> getPleiepengerPerioder() { return Optional.ofNullable(pleiepengerPerioder); }
}
