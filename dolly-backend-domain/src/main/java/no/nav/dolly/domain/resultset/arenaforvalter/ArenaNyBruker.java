package no.nav.dolly.domain.resultset.arenaforvalter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArenaNyBruker {

    private String personident;
    private String miljoe;
    private ArenaKvalifiseringsgruppe kvalifiseringsgruppe;
    private ArenaBrukerUtenServicebehov utenServicebehov;
    private List<ArenaAap115> aap115;
    private List<ArenaAap> aap;
}
