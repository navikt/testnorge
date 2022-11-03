package no.nav.testnav.apps.syntaaregservice.domain.aareg;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsUtenlandsopphold {

    private String land;
    private RsPeriode periode;
}
