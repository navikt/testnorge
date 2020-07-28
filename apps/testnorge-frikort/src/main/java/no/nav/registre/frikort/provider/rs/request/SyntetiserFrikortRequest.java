package no.nav.registre.frikort.provider.rs.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SyntetiserFrikortRequest {

    private Long avspillergruppeId;
    private String miljoe;
    private int antallNyeIdenter;
}
