package no.nav.testnav.apps.syntaaregservice.domain.synt;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsSyntetiskAntallTimerForTimeloennet {

    private Integer antallTimer;
    private RsSyntetiskPeriode periode;
}
