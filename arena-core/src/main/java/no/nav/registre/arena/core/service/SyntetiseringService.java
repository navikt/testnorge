package no.nav.registre.arena.core.service;


import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.consumer.rs.ArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.HodejegerenConsumer;
import no.nav.registre.arena.core.consumer.rs.responses.Arbeidsoker;
import no.nav.registre.arena.core.consumer.rs.responses.StatusFraArenaForvalterResponse;
import no.nav.registre.arena.core.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.arena.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.floor;

@Service
@Slf4j
public class SyntetiseringService {

    private static final double PROSENTANDEL_SOM_SKAL_HA_MELDEKORT = 0.2;

    @Autowired
    HodejegerenConsumer hodejegerenConsumer;
    @Autowired
    ArenaForvalterConsumer arenaForvalterConsumer;
    @Autowired
    Random random;

    public ResponseEntity registrerBrukereIArenaForvalter(SyntetiserArenaRequest arenaRequest, Integer antallNyeIdenter) {

        List<String> nyeIdenter = hentGyldigeIdenter(arenaRequest, antallNyeIdenter);
        NyeBrukereList nyeBrukere = opprettNyeBrukere(nyeIdenter, arenaRequest.getMiljoe());

        StatusFraArenaForvalterResponse response = arenaForvalterConsumer.sendTilArenaForvalter(nyeBrukere);
        return byggOpprettedeBrukereResponse(response);
    }

    public ResponseEntity fyllOppBrukereIArenaForvalter(SyntetiserArenaRequest arenaRequest) {
        double levendeIdenter = hodejegerenConsumer.finnLevendeIdenterOverAlder(arenaRequest.getAvspillergruppeId()).size();
        double eksisterendeIdenter = arenaForvalterConsumer.hentEksisterendeIdenter().size();

        int antallNyeIdenterSomSkalHaMeldekort = (int) (floor(levendeIdenter * PROSENTANDEL_SOM_SKAL_HA_MELDEKORT) - eksisterendeIdenter);

        if (antallNyeIdenterSomSkalHaMeldekort > 0) {
            return registrerBrukereIArenaForvalter(arenaRequest, antallNyeIdenterSomSkalHaMeldekort);
        }
        return ResponseEntity.ok().body("Minst 20% identer hadde allerede meldekort. Ingen ble lagt til i ArenaForvalter.");
    }

    public ResponseEntity slettBrukereIArenaForvalter(SyntetiserArenaRequest arenaRequest) {
        log.error("Denne metoden er ikke implementert enda.");
        return null;
    }


    private ResponseEntity byggOpprettedeBrukereResponse(StatusFraArenaForvalterResponse response) {
        StringBuilder status = new StringBuilder();

        if (!CollectionUtils.isEmpty(response.getArbeidsokerList())) {
            status.append("Nye identer opprettet med meldekort: \n")
                    .append(response.getArbeidsokerList().stream().map(Arbeidsoker::getPersonident))
                    .append(". ");
        }

        return ResponseEntity.ok().body(status.toString());
    }

    private List<String> hentGyldigeIdenter(SyntetiserArenaRequest arenaRequest, int antallNyeIdenter) {
        List<String> levendeIdenter = hodejegerenConsumer.finnLevendeIdenterOverAlder(arenaRequest.getAvspillergruppeId());
        List<String> eksisterendeIdenter = arenaForvalterConsumer.hentEksisterendeIdenter();

        levendeIdenter.removeAll(eksisterendeIdenter);

        if (antallNyeIdenter > levendeIdenter.size()) {
            antallNyeIdenter = levendeIdenter.size();
            log.info("Fant ikke nok ledige identer i avspillergruppe. Lager meldekort på {} nye identer.", antallNyeIdenter);
        }

        List<String> nyeIdenter = new ArrayList<>(antallNyeIdenter);

        for (int i = 0; i < antallNyeIdenter; i++) { // TODO: sjekke om dette går hvis antallNyeIdenter == levendeIdenter.size()
            nyeIdenter.add(levendeIdenter.remove(random.nextInt(levendeIdenter.size())));
        }

        return nyeIdenter;
    }
    private List<String> hentGyldigeIdenter(SyntetiserArenaRequest arenaRequest) {

    }

    private NyeBrukereList opprettNyeBrukere(List<String> identerFraHodejegeren, String miljoe) {

        List<NyBruker> nyeBrukere = new ArrayList<>(identerFraHodejegeren.size());

        for (String ident : identerFraHodejegeren) {
            nyeBrukere.add(new NyBruker(
                    ident,
                    miljoe,
                    "IKVAL",
                    null,
                    true,
                    null,
                    null
            ));
        }

        return new NyeBrukereList(nyeBrukere);
    }


}
