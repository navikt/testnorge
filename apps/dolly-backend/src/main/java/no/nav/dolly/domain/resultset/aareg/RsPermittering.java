package no.nav.dolly.domain.resultset.aareg;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsPermittering {

    @Schema(required = true)
    private RsPeriodeAareg permitteringsPeriode;

    @Schema(required = true)
    private BigDecimal permitteringsprosent;
}
