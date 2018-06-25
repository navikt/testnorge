package no.nav.resultSet;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class RsBrukerMedTeamsOgFavoritter {
    private RsBruker bruker;
    private Set<RsTeam> teams;
}
