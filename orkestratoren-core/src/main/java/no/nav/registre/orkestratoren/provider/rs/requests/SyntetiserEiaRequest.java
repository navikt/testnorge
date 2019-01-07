package no.nav.registre.orkestratoren.provider.rs.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SyntetiserEiaRequest {

    private Long skdMeldingGruppeId;
    private String miljoe;
    private int antallMeldinger;
}
