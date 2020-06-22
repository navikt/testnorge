package no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@ApiModel
@Builder
@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
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
    @ApiModelProperty("For inntektsmeldingstype 201812 må enten arbeidsgiver eller arbeidsgiverPrivat være satt.")
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
    private List<RsNaturalytelseDetaljer> opphoerAvNaturalytelseListe;
    @JsonProperty
    @ApiModelProperty()
    private List<RsNaturalytelseDetaljer> gjenopptakelseNaturalytelseListe;
    @JsonProperty
    @ApiModelProperty()
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
