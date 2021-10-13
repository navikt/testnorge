package no.nav.registre.sigrun.provider.rs.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SyntetiserSigrunRequest {

    private Long avspillergruppeId;
    private String miljoe;
    private int antallNyeIdenter;
}
