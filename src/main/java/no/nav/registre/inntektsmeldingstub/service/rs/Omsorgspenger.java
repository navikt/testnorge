package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class Omsorgspenger {

    @JsonProperty
    @ApiModelProperty()
    private boolean harUtbetaltPliktigeDager;
    @JsonProperty
    @ApiModelProperty()
    private List<Periode> fravaersPerioder;
    @JsonProperty
    @ApiModelProperty()
    private List<DelvisFravaer> delvisFravaersListe;

}
