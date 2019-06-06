package no.nav.dolly.domain.resultset.arenaforvalter;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArenaNyeBrukere {

    private List<ArenaNyBruker> nyeBrukere;

    public List<ArenaNyBruker> getNyeBrukere() {
        if (isNull(nyeBrukere)) {
            nyeBrukere = new ArrayList<>();
        }
        return nyeBrukere;
    }
}
