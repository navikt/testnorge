package no.nav.registre.inntekt.domain.altinn;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.nav.registre.inntekt.domain.altinn.enums.NaturalytelseKodeListe;

import java.time.LocalDate;

@ApiModel
@Builder
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class RsAltinnNaturalytelseDetaljer {
    @JsonProperty
    @ApiModelProperty
    private NaturalytelseKodeListe naturalytelseType;
    @JsonProperty
    @ApiModelProperty(value = "Startdato for naturaytelse", example = "YYYY-MM-DD")
    private LocalDate fom;
    @JsonProperty
    @ApiModelProperty("Samlet månedlig beløp for naturaytelsen")
    private Double beloepPrMnd;

}
