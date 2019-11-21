package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class NaturaYtelseDetaljer {

    @JsonProperty
    @ApiModelProperty()
    private String naturaytelseType;
    @JsonProperty
    @ApiModelProperty()
    private Date fom;
    @JsonProperty
    @ApiModelProperty()
    private double beloepPrMnd;

}
