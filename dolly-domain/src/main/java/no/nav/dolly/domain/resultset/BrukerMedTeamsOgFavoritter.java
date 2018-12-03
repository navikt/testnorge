package no.nav.dolly.domain.resultset;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;

@Getter
@Setter
@Builder
public class BrukerMedTeamsOgFavoritter {
    private Bruker bruker;
    private List<Team> teams;
}
