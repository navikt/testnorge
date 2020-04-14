package no.nav.registre.testnorge.elsam.provider.rs.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SyntetiserElsamRequest {

    private Long avspillergruppeId;
    private String miljoe;
    private int antallIdenter;
}
