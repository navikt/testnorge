package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RsInntektsmelding {

    private String ytelse;
    private String aarsakTilInnsending;
    @Size(min = 11, max = 11)
    private String arbeidstakerFnr;
    private boolean naerRelasjon;
    private RsAvsendersystem avsendersystem;

    private RsArbeidsgiver arbeidsgiver;
    private RsArbeidsgiverPrivat arbeidsgiverPrivat;

    private RsArbeidsforhold arbeidsforhold;

    private RsRefusjon refusjon;
    private RsOmsorgspenger omsorgspenger;
    private RsSykepengerIArbeidsgiverperioden sykepengerIArbeidsgiverperioden;
    private String startdatoForeldrepengeperiode;
    private List<RsNaturalytelseDetaljer> opphoerAvNaturalytelseListe;
    private List<RsNaturalytelseDetaljer> gjenopptakelseNaturalytelseListe;
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
