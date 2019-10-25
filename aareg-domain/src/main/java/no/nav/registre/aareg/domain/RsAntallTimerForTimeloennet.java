package no.nav.registre.aareg.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsAntallTimerForTimeloennet {

    private Integer antallTimer;
    private RsPeriode periode;
}
