package no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RsInntektsmelding {

    @JsonProperty
    private String ytelse;
    @JsonProperty
    private String aarsakTilInnsending;
    @JsonProperty
    @Size(min = 11, max = 11)
    private String arbeidstakerFnr;
    @JsonProperty
    private boolean naerRelasjon;
    @JsonProperty
    private RsAvsendersystem avsendersystem;

    @JsonProperty
    private RsArbeidsgiver arbeidsgiver;
    @JsonProperty
    private RsArbeidsgiverPrivat arbeidsgiverPrivat;

    @JsonProperty
    private RsArbeidsforhold arbeidsforhold;

    @JsonProperty
    private RsRefusjon refusjon;
    @JsonProperty
    private RsOmsorgspenger omsorgspenger;
    @JsonProperty
    private RsSykepengerIArbeidsgiverperioden sykepengerIArbeidsgiverperioden;
    @JsonProperty
    private LocalDate startdatoForeldrepengeperiode;
    @JsonProperty
    private List<RsNaturalytelseDetaljer> opphoerAvNaturalytelseListe;
    @JsonProperty
    private List<RsNaturalytelseDetaljer> gjenopptakelseNaturalytelseListe;
    @JsonProperty
    private List<RsPeriode> pleiepengerPerioder;

    public Optional<RsArbeidsgiver> getArbeidsgiver() {
        return Optional.ofNullable(arbeidsgiver);
    }

    public Optional<RsArbeidsgiverPrivat> getArbeidsgiverPrivat() {
        return Optional.ofNullable(arbeidsgiverPrivat);
    }

    public Optional<RsRefusjon> getRefusjon() {
        return Optional.ofNullable(refusjon);
    }

    public Optional<RsOmsorgspenger> getOmsorgspenger() {
        return Optional.ofNullable(omsorgspenger);
    }

    public Optional<RsSykepengerIArbeidsgiverperioden> getSykepengerIArbeidsgiverPerioden() {
        return Optional.ofNullable(sykepengerIArbeidsgiverperioden);
    }

    public Optional<LocalDate> getStartdatoForeldrepengeperiode() {
        return Optional.ofNullable(startdatoForeldrepengeperiode);
    }

    public Optional<List<RsNaturalytelseDetaljer>> getOpphoerAvNaturalytelseListe() {
        return Optional.ofNullable(opphoerAvNaturalytelseListe);
    }

    public Optional<List<RsNaturalytelseDetaljer>> getGjenopptakelseNaturalytelseListe() {
        return Optional.ofNullable(gjenopptakelseNaturalytelseListe);
    }

    public Optional<List<RsPeriode>> getPleiepengerPerioder() {
        return Optional.ofNullable(pleiepengerPerioder);
    }
}
