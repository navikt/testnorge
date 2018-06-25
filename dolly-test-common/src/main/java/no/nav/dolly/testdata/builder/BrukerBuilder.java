package no.nav.dolly.testdata.builder;

import lombok.Builder;
import no.nav.jpa.Bruker;

@Builder
public class BrukerBuilder {

    private String navIdent;

    public Bruker convertToRealBruker(){
        Bruker bruker = new Bruker();
        bruker.setNavIdent(this.navIdent);

        return bruker;
    }
}
