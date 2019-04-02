package no.nav.dolly.domain.resultset.aareg;

import java.math.BigDecimal;

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

    private RsPeriode periode;
    private BigDecimal antallTimer;
}
