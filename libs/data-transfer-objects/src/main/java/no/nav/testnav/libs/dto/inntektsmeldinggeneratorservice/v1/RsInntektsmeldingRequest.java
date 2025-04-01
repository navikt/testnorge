package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.AarsakInnsendingKodeListe;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.YtelseKodeListe;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsArbeidsforhold;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsArbeidsgiver;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsArbeidsgiverPrivat;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsAvsendersystem;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsNaturalytelseDetaljer;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsOmsorgspenger;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsPeriode;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsRefusjon;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsSykepengerIArbeidsgiverperioden;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class RsInntektsmeldingRequest {

    private YtelseKodeListe ytelse;

    private AarsakInnsendingKodeListe aarsakTilInnsending;

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
