package no.nav.registre.inntekt.consumer.rs.altinninntekt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.enums.AarsakInnsendingKodeListe;
import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.enums.YtelseKodeListe;
import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.rs.RsArbeidsforhold;
import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.rs.RsArbeidsgiver;
import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.rs.RsArbeidsgiverPrivat;
import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.rs.RsAvsendersystem;
import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.rs.RsNaturalytelseDetaljer;
import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.rs.RsOmsorgspenger;
import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.rs.RsPeriode;
import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.rs.RsRefusjon;
import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.rs.RsSykepengerIArbeidsgiverperioden;

/**
 * Skal være så å si et duplikat av RsInntektsmelding, men uten fnr da denne blir satt av
 * business logikk når hver melding skal lages.
 */
@ApiModel
@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class RsInntektsmeldingRequest {

    @JsonProperty
    @ApiModelProperty(required = true)
    private YtelseKodeListe ytelse;

    @JsonProperty
    @ApiModelProperty(required = true)
    private AarsakInnsendingKodeListe aarsakTilInnsending;

    @JsonProperty
    @ApiModelProperty(value = "default = \'false\'", required = true)
    private boolean naerRelasjon;

    @JsonProperty
    @ApiModelProperty
    private RsAvsendersystem avsendersystem;

    @JsonProperty
    @ApiModelProperty
    private RsArbeidsgiver arbeidsgiver;

    @JsonProperty
    @ApiModelProperty
    private RsArbeidsgiverPrivat arbeidsgiverPrivat;

    @JsonProperty
    @ApiModelProperty(value = "Gjeldende arbeidsforhold. \'nillable\' i XSD skjema.", required = true)
    private RsArbeidsforhold arbeidsforhold;

    @JsonProperty
    @ApiModelProperty
    private RsRefusjon refusjon;

    @JsonProperty
    @ApiModelProperty
    private RsOmsorgspenger omsorgspenger;

    @JsonProperty
    @ApiModelProperty
    private RsSykepengerIArbeidsgiverperioden sykepengerIArbeidsgiverperioden;

    @JsonProperty
    @ApiModelProperty(example = "YYYY-MM-DD")
    private LocalDate startdatoForeldrepengeperiode;

    @JsonProperty
    @ApiModelProperty
    private List<RsNaturalytelseDetaljer> opphoerAvNaturalytelseListe;

    @JsonProperty
    @ApiModelProperty
    private List<RsNaturalytelseDetaljer> gjenopptakelseNaturalytelseListe;

    @JsonProperty
    @ApiModelProperty
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
