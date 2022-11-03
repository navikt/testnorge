package no.nav.testnav.apps.syntaaregservice.domain.synt;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsSyntetiskUtenlandsopphold {

    private String land;
    private RsSyntetiskPeriode periode;
}
