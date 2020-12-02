package no.nav.registre.testnorge.identservice.testdata.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IdentResponse {

    private String ident;
    private boolean inProd;

}
