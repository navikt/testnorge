package no.nav.testnav.apps.syntaaregservice.domain.aareg;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsAntallTimerForTimeloennet {

    private Double antallTimer;
    private RsPeriode periode;
}
