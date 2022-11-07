package no.nav.registre.sdforvalter.consumer.rs.request.syntetisering;

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
