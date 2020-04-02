package no.nav.registre.inntekt.domain.altinn.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@Builder
@AllArgsConstructor
public class RsBeregnetInntekt {
    @JsonProperty
    @ApiModelProperty("Månedsinntekt")
    private Double beloep;
    @JsonProperty
    @ApiModelProperty
    private String aarsakVedEndring;
}
