package no.nav.registre.sigrun.provider.rs.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SyntetiserPoppRequest {

    private Long avspillergruppeId;
    private String miljoe;
    private int antallNyeIdenter;
}
