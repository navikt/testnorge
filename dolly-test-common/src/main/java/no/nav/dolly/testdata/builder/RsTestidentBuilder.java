package no.nav.dolly.testdata.builder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import no.nav.dolly.domain.resultset.RsTestident;

@Getter
@Setter
@Builder
public class RsTestidentBuilder {

    private String ident;

    public RsTestident convertToRealRsTestident(){
        RsTestident testident = new RsTestident();
        testident.setIdent(this.ident);

        return testident;
    }
}
