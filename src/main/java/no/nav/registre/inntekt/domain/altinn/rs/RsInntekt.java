package no.nav.registre.inntekt.domain.altinn.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@ApiModel
@Builder
@Value
@NoArgsConstructor(force = true)
public class RsInntekt {
    @JsonProperty
    @ApiModelProperty("Månedsinntekt")
    private Double beloep;
    @JsonProperty
    @ApiModelProperty
    private String aarsakVedEndring;
}
