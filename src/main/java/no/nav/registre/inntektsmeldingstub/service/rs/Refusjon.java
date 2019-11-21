package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

public class Refusjon {

    @JsonProperty
    @ApiModelProperty()
    private double refusjonsbeloepPrMnd;
    @JsonProperty
    @ApiModelProperty()
    private Date refusjonsopphoersdato;
    @JsonProperty
    @ApiModelProperty()
    private List<EndringIRefusjon> endringIRefusjonListe;

}
