package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class RsEndringIRefusjon {

    @JsonProperty
    @ApiModelProperty()
    private Date endringsdato;
    @JsonProperty
    @ApiModelProperty()
    private double refusjonsbeloepPrMnd;

}
