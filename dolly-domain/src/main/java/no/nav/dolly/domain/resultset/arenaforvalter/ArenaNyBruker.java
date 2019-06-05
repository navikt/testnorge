package no.nav.dolly.domain.resultset.arenaforvalter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArenaNyBruker {

    private String personident;
    private String miljoe;
    private ArenaKvalifiseringsgruppe arenaKvalifiseringsgruppe;
    private ArenaBrukerUtenServicebehov utenServicebehov;
}
