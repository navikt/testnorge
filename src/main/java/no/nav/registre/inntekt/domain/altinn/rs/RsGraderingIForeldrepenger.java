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
public class RsGraderingIForeldrepenger {

    @JsonProperty
    @ApiModelProperty("Periode med foreldrepenger")
    private RsPeriode periode;
    @JsonProperty
    @ApiModelProperty("Heltall med arbeidstidsprosent")
    private Integer arbeidstidprosent;

}
