package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class ArbeidsgiverPrivat {

    @JsonProperty
    @ApiModelProperty(required = true)
    private String arbeidsgiverFnr;
    @JsonProperty
    @ApiModelProperty(required = true)
    private Kontaktinformasjon kontaktinformasjon;

}
