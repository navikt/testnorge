package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class RsOmsorgspenger {

    @JsonProperty
    @ApiModelProperty()
    private boolean harUtbetaltPliktigeDager;
    @JsonProperty
    @ApiModelProperty()
    private List<RsPeriode> fravaersPerioder;
    @JsonProperty
    @ApiModelProperty()
    private List<RsDelvisFravaer> delvisFravaersListe;

}
