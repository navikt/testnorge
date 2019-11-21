package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class Kontaktinformasjon {

    @JsonProperty
    @ApiModelProperty(required = true)
    private String kontaktinformasjonNavn;
    @JsonProperty
    @ApiModelProperty(required = true)
    private String telefonnummer;

}
