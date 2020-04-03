package no.nav.registre.inntekt.domain.altinn;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.nav.registre.inntekt.domain.altinn.enums.AarsakUtsettelseKodeListe;
import no.nav.registre.inntekt.domain.altinn.rs.RsPeriode;

@ApiModel
@Builder
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class RsAltinnUtsettelseAvForeldrepenger {

    @JsonProperty
    @ApiModelProperty("Periode for utsettelse av foreldrepenger")
    private RsPeriode periode;
    @JsonProperty
    @ApiModelProperty()
    private AarsakUtsettelseKodeListe aarsakTilUtsettelse;
}
