package no.nav.dolly.domain.resultset.aareg;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsAntallTimerIPerioden {

    @ApiModelProperty(
            position = 1,
            required = true
    )
    private RsPeriode periode;

    @ApiModelProperty(
            position = 2,
            required = true
    )
    private BigDecimal antallTimer;
}
