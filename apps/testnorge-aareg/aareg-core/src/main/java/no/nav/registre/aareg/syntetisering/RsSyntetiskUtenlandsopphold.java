package no.nav.registre.aareg.syntetisering;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsSyntetiskUtenlandsopphold {

    private String land;
    private RsSyntetiskPeriode periode;
}
