package no.nav.dolly.domain.resultset;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;

import java.util.List;

@Getter
@Setter
@Builder
public class BrukerMedTeamsOgFavoritter {
    private Bruker bruker;
    private List<Team> teams;
}
