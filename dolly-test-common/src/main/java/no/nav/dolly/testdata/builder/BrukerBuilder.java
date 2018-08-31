package no.nav.dolly.testdata.builder;

import lombok.Builder;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;

import java.util.Set;

@Builder
public class BrukerBuilder {

    private String navIdent;
    private Set<Testgruppe> favoritter;
    private Set<Team> teams;

    public Bruker convertToRealBruker(){
        Bruker bruker = new Bruker();
        bruker.setNavIdent(this.navIdent);
        bruker.setFavoritter(this.favoritter);
        bruker.setTeams(this.teams);

        return bruker;
    }
}
