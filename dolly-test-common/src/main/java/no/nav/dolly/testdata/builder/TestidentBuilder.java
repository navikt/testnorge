package no.nav.dolly.testdata.builder;

import lombok.Builder;
import no.nav.jpa.Testgruppe;
import no.nav.jpa.Testident;

@Builder
public class TestidentBuilder {

    private String ident;
    private Testgruppe testgruppe;

    public Testident convertToRealTestident(){
        Testident testident = new Testident();
        testident.setIdent(this.ident);
        testident.setTestgruppe(this.testgruppe);

        return testident;
    }
}
