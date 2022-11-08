package no.nav.registre.sdforvalter.consumer.rs.request.syntetisering;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import no.nav.testnav.libs.dto.aareg.v1.AntallTimerForTimeloennet;
import static java.util.Objects.isNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsSyntetiskAntallTimerForTimeloennet {

    private Integer antallTimer;
    private RsSyntetiskPeriode periode;

    @JsonIgnore
    public AntallTimerForTimeloennet toAntallTimerForTimeloennet() {
        return AntallTimerForTimeloennet.builder()
                .antallTimer(isNull(antallTimer) ? null : Double.valueOf(antallTimer))
                .periode(isNull(periode) ? null :periode.toPeriode())
                .build();
    }
}
