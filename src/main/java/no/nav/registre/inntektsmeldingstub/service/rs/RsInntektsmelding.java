package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@ApiModel
@Builder
@Getter
@NoArgsConstructor
public class RsInntektsmelding {

    @JsonProperty
    @ApiModelProperty(required = true)
    private String ytelse;
    @JsonProperty
    @ApiModelProperty(required = true)
    private String aarsakTilInnsending;
    @JsonProperty
    @ApiModelProperty(required = true)
    private String arbeidstakerFnr;
    @JsonProperty
    @ApiModelProperty(required = true)
    private boolean naerRelasjon;
    @JsonProperty
    @ApiModelProperty(required = true)
    private RsAvsendersystem avsendersystem;

    // en av disse må være satt
    @JsonProperty
    @ApiModelProperty()
    private RsArbeidsgiver arbeidsgiver;
    @JsonProperty
    @ApiModelProperty()
    private RsArbeidsgiverPrivat arbeidsgiverPrivat;

    @JsonProperty
    @ApiModelProperty()
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
    @ApiModelProperty()
    private Date startdatoForeldrepengeperiode;
    @JsonProperty
    @ApiModelProperty()
    private List<RsNaturaYtelseDetaljer> opphoerAvNaturalytelseListe;
    @JsonProperty
    @ApiModelProperty()
    private List<RsNaturaYtelseDetaljer> gjenopptakelseNaturalytelseListe;
    @JsonProperty
    @ApiModelProperty()
    private List<RsPeriode> pleiepengerPerioder;

}
