package no.nav.registre.aareg.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsUtenlandsopphold {

    private String land;
    private RsPeriode periode;
}
