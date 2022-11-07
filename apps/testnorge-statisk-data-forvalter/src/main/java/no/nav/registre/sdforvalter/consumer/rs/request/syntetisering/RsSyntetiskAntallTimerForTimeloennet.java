package no.nav.registre.sdforvalter.consumer.rs.request.syntetisering;

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
