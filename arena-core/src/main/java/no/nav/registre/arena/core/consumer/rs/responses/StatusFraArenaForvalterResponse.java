package no.nav.registre.arena.core.consumer.rs.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import no.nav.registre.arena.domain.brukere.Arbeidsoeker;
import no.nav.registre.arena.domain.brukere.NyBrukerFeil;

@Getter
@Setter
@NoArgsConstructor
public class StatusFraArenaForvalterResponse {

    private List<Arbeidsoeker> arbeidsokerList;
    private List<NyBrukerFeil> nyBrukerFeilList;
    private int antallSider;
}