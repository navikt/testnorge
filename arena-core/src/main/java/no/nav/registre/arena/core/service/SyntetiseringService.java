package no.nav.registre.arena.core.service;


import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.consumer.rs.ArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.HodejegerenConsumer;
import no.nav.registre.arena.core.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.arena.domain.NyeBrukereList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class SyntetiseringService {
    @Autowired
    HodejegerenConsumer hodejegerenConsumer;
    @Autowired
    ArenaForvalterConsumer arenaForvalterConsumer;
    @Autowired
    Random random;

    public ResponseEntity registrerBrukereIArenaForvalter(SyntetiserArenaRequest arenaRequest) {

        List<String> levendeIdenter = hodejegerenConsumer.finnLevendeIdenterOverAlder(arenaRequest.getAvspillergruppeId());
        List<String> nyeIdenter = new ArrayList<>(arenaRequest.getAntallNyeIdenter());
        List<String> eksisterendeIdenter = arenaForvalterConsumer.hentEksisterendeIdenter();

        levendeIdenter.removeAll(eksisterendeIdenter);
        int antallNyeIdenter = arenaRequest.getAntallNyeIdenter();

        if (antallNyeIdenter > levendeIdenter.size()) {
            antallNyeIdenter = levendeIdenter.size();
            log.info("Fant ikke nok ledige identer i avspillergruppe. Lager meldekort på {} nye identer.", antallNyeIdenter);
        }

        for (int i = 0; i < antallNyeIdenter; i++) { // TODO: sjekke om dette går hvis antallNyeIdenter == levendeIdenter.size()
            nyeIdenter.add(levendeIdenter.remove(random.nextInt(levendeIdenter.size())));
        }



        return null;
    }

    private NyeBrukereList genererNyeBrukere(List<String> identer) {

    }


}
