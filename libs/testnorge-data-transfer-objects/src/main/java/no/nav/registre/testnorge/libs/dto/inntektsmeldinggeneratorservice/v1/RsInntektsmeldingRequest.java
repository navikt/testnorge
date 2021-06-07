package no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.enums.YtelseKodeListe;
import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.enums.AarsakInnsendingKodeListe;
import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsArbeidsforhold;
import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsArbeidsgiver;
import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsArbeidsgiverPrivat;
import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsAvsendersystem;
import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsOmsorgspenger;
import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsPeriode;
import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsRefusjon;
import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsSykepengerIArbeidsgiverperioden;
import no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsNaturalytelseDetaljer;


@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class RsInntektsmeldingRequest {

    @JsonProperty
    private YtelseKodeListe ytelse;

    @JsonProperty
    private AarsakInnsendingKodeListe aarsakTilInnsending;

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
