package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class GraderingAvForeldrepenger {

    @JsonProperty
    @ApiModelProperty()
    private Periode periode;
    @JsonProperty
    @ApiModelProperty()
    private Integer arbeidstidprosent;

}
