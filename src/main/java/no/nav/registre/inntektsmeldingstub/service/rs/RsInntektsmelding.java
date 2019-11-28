package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    public Optional<RsArbeidsgiver> getArbeidsgiver() { return Optional.ofNullable(arbeidsgiver); }
    public Optional<RsArbeidsgiverPrivat> getArbeidsgiverPrivat() { return Optional.ofNullable(arbeidsgiverPrivat); }
    public Optional<RsRefusjon> getRefusjon() { return Optional.ofNullable(refusjon); }
    public Optional<RsOmsorgspenger> getOmsorgspenger() { return Optional.ofNullable(omsorgspenger); }
    public Optional<RsSykepengerIArbeidsgiverperioden> getSykepengerIArbeidsgiverPerioden() { return Optional.ofNullable(sykepengerIArbeidsgiverperioden); }
    public Optional<LocalDate> getStartdatoForeldrepengeperiode() { return Optional.ofNullable(startdatoForeldrepengeperiode); }
    public Optional<List<RsNaturaYtelseDetaljer>> getOpphoerAvNaturalytelseListe() { return Optional.ofNullable(opphoerAvNaturalytelseListe); }
    public Optional<List<RsNaturaYtelseDetaljer>> getGjenopptakelseNaturalytelseListe() { return Optional.ofNullable(gjenopptakelseNaturalytelseListe); }
    public Optional<List<RsPeriode>> getPleiepengerPerioder() { return Optional.ofNullable(pleiepengerPerioder); }
}
