package no.nav.registre.inntekt.domain.altinn;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.nav.registre.inntekt.domain.altinn.enums.AarsakBeregnetInntektEndringKodeListe;

@ApiModel
@Builder
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class RsAltinnInntekt {

    @JsonProperty
    @ApiModelProperty("Månedsinntekt")
    private Double beloep;
    @JsonProperty
    @ApiModelProperty
    private AarsakBeregnetInntektEndringKodeListe aarsakVedEndring;

}
