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
public class Inntektsmelding {

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
    private Avsendersystem avsendersystem;

    // en av disse må være satt
    @JsonProperty
    @ApiModelProperty()
    private Arbeidsgiver arbeidsgiver;
    @JsonProperty
    @ApiModelProperty()
    private ArbeidsgiverPrivat arbeidsgiverPrivat;

    @JsonProperty
    @ApiModelProperty()
    private Arbeidsforhold arbeidsforhold;

    @JsonProperty
    @ApiModelProperty()
    private Refusjon refusjon;
    @JsonProperty
    @ApiModelProperty()
    private Omsorgspenger omsorgspenger;
    @JsonProperty
    @ApiModelProperty()
    private SykepengerIArbeidsgiverperioden sykepengerIArbeidsgiverperioden;
    @JsonProperty
    @ApiModelProperty()
    private Date startdatoForeldrepengeperiode;
    @JsonProperty
    @ApiModelProperty()
    private List<NaturaYtelseDetaljer> opphoerAvNaturalytelseListe;
    @JsonProperty
    @ApiModelProperty()
    private List<NaturaYtelseDetaljer> gjenopptakelseNaturalytelseListe;
    @JsonProperty
    @ApiModelProperty()
    private List<Periode> pleiepengerPerioder;

}
