package no.nav.registre.inntekt.domain.altinn.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@ApiModel
@Builder
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class RsUtsettelseAvForeldrepenger {

    @JsonProperty
    @ApiModelProperty("Periode for utsettelse av foreldrepenger")
    private RsPeriode periode;
    @JsonProperty
    @ApiModelProperty()
    private String aarsakTilUtsettelse;

}
