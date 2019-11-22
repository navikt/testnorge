package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class RsDelvisFravaer {

    @JsonProperty
    @ApiModelProperty()
    private Date dato;
    @JsonProperty
    @ApiModelProperty()
    private double timer;

}
