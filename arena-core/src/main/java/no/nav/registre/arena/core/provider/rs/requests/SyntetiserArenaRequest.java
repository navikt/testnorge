package no.nav.registre.arena.core.provider.rs.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SyntetiserArenaRequest {

    private Long avspillergruppeId;
    private String miljoe;
    private Integer antallNyeIdenter;
}