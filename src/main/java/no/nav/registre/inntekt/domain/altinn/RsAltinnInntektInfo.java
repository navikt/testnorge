package no.nav.registre.inntekt.domain.altinn;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.nav.registre.inntekt.domain.altinn.rs.RsArbeidsforhold;
import no.nav.registre.inntekt.domain.altinn.rs.RsArbeidsgiver;
import no.nav.registre.inntekt.domain.altinn.rs.RsAvsendersystem;
import no.nav.registre.inntekt.domain.altinn.rs.RsNaturalytelseDetaljer;
import no.nav.registre.inntekt.domain.altinn.rs.RsOmsorgspenger;
import no.nav.registre.inntekt.domain.altinn.rs.RsPeriode;
import no.nav.registre.inntekt.domain.altinn.rs.RsRefusjon;
import no.nav.registre.inntekt.domain.altinn.rs.RsSykepengerIArbeidsgiverperioden;

import java.time.LocalDate;
import java.util.List;


/**
 * Skal være så å si et duplikat av RsInntektsmelding, men uten fnr og privatarbeidsgiver da disse blir satt av
 * business logikk når hver melding skal lages.
 */
@ApiModel
@Value
@NoArgsConstructor(force = true)
public class RsAltinnInntektInfo {
    @JsonProperty
    @ApiModelProperty(required = true)
    private String ytelse;
    @JsonProperty
    @ApiModelProperty(required = true)
    private String aarsakTilInnsending;
    @JsonProperty
    @ApiModelProperty(value = "default = \'false\'", required = true)
    private boolean naerRelasjon;

    @JsonProperty
    @ApiModelProperty
    private RsAvsendersystem avsendersystem;
    @JsonProperty
    @ApiModelProperty(required = true)
    private RsArbeidsgiver arbeidsgiver;
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
}
