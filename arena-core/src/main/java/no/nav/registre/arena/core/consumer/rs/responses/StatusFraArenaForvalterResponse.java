package no.nav.registre.arena.core.consumer.rs.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.arena.domain.Arbeidsoeker;
import no.nav.registre.arena.domain.NyBrukerFeil;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StatusFraArenaForvalterResponse {
    private List<Arbeidsoeker> arbeidsokerList;
    private List<NyBrukerFeil> nyBrukerFeilList;
    private int antallSider;
}