package no.nav.registre.tss.provider.rs.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyntetiserTssRequest {

    private Long avspillergruppeId;
    private String miljoe;
    private int antallNyeIdenter;
}
