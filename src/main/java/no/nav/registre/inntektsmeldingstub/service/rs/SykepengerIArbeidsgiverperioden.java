package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class SykepengerIArbeidsgiverperioden {

    @JsonProperty
    @ApiModelProperty()
    private List<Periode> arbeidsgiverperiodeListe;
    @JsonProperty
    @ApiModelProperty()
    private double bruttoUtbetalt;
    @JsonProperty
    @ApiModelProperty()
    private String begrunnelseForReduksjonEllerIkkeUtbetalt;

}
