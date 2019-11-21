package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class Periode {

    @JsonProperty
    @ApiModelProperty(value = "Dato fra og med", example = "YYYY-MM-DD")
    private Date fom;
    @JsonProperty
    @ApiModelProperty(value = "Dato til og med", example = "YYYY-MM-DD")
    private Date tom;

}
