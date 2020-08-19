package no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.enums.AarsakBeregnetInntektEndringKodeListe;

@ApiModel
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
    private AarsakBeregnetInntektEndringKodeListe aarsakVedEndring;
}
