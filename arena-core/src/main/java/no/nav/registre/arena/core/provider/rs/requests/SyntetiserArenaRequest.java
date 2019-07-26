package no.nav.registre.arena.core.provider.rs.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SyntetiserArenaRequest {

    private Long avspillergruppeId;
    private String miljoe;
    private Integer antallNyeIdenter;
}