package no.nav.testnav.apps.syntaaregservice.provider.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SyntetiserAaregRequest {

    private Long avspillergruppeId;
    private String miljoe;
    private int antallNyeIdenter;
}
