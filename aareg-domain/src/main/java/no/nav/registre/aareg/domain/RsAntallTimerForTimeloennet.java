package no.nav.registre.aareg.domain;

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
public class RsAntallTimerForTimeloennet {

    public Integer antallTimer;
    public RsPeriode periode;
}
