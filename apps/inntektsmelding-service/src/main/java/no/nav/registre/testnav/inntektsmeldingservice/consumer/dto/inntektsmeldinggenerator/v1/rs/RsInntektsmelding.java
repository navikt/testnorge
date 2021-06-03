package no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.enums.YtelseKodeListe;
import no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.enums.AarsakInnsendingKodeListe;

@Builder
@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class RsInntektsmelding {

    @JsonProperty
    private YtelseKodeListe ytelse;

    @JsonProperty
    private AarsakInnsendingKodeListe aarsakTilInnsending;

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

    public RsAvsendersystem getAvsendersystem() {
        return Objects.requireNonNullElse(avsendersystem, new RsAvsendersystem());
    }

    public List<RsNaturalytelseDetaljer> getOpphoerAvNaturalytelseListe() {
        return Objects.requireNonNullElse(opphoerAvNaturalytelseListe, Collections.emptyList());
    }

    public List<RsNaturalytelseDetaljer> getGjenopptakelseNaturalytelseListe() {
        return Objects.requireNonNullElse(gjenopptakelseNaturalytelseListe, Collections.emptyList());
    }

    public List<RsPeriode> getPleiepengerPerioder() {
        return Objects.requireNonNullElse(pleiepengerPerioder, Collections.emptyList());
    }
}
