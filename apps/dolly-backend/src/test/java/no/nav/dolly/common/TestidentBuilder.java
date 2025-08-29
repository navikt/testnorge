package no.nav.dolly.common;

import lombok.Builder;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;

@Builder
public class TestidentBuilder {

    private String ident;
    private Testgruppe testgruppe;

    public Testident convertToRealTestident() {
        Testident testident = new Testident();
        testident.setIdent(this.ident);
        testident.setTestgruppe(this.testgruppe);

        return testident;
    }
}
