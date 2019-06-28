package no.nav.dolly.domain.resultset.aareg;

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
public class RsAntallTimerIPerioden {

    private RsPeriode periode;
    private BigDecimal antallTimer;
}
