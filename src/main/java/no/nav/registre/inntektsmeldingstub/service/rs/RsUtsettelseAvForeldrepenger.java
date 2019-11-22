package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class RsUtsettelseAvForeldrepenger {

    @JsonProperty
    @ApiModelProperty()
    private RsPeriode periode;
    @JsonProperty
    @ApiModelProperty()
    private String aarsakTilUtsettelse;

}
