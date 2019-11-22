package no.nav.registre.inntektsmeldingstub.service.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RsUtsettelseAvForeldrepenger {

    @JsonProperty
    @ApiModelProperty("Periode for utsettelse av foreldrepenger")
    private RsPeriode periode;
    @JsonProperty
    @ApiModelProperty()
    private String aarsakTilUtsettelse;

}
