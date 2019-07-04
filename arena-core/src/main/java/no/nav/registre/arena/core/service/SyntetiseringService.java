package no.nav.registre.arena.core.service;


import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.consumer.rs.ArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.HodejegerenConsumer;
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

@Service
@Slf4j
public class SyntetiseringService {

    private static final double PROSENTANDEL_MED_MELDEKORT = 0.2;

    @Autowired
    HodejegerenConsumer hodejegerenConsumer;
    @Autowired
    ArenaForvalterConsumer arenaForvalterConsumer;
    @Autowired
    Random random;

    public ResponseEntity registrerBrukereIArenaForvalter(SyntetiserArenaRequest arenaRequest) {

        List<String> nyeIdenter = hentGyldigeIdenter(arenaRequest);
        NyeBrukereList nyeBrukere = opprettNyeBrukere(nyeIdenter, arenaRequest.getMiljoe());

        StatusFraArenaForvalterResponse response = arenaForvalterConsumer.sendTilArenaForvalter(nyeBrukere);
        return byggResponse(response);
    }

    private ResponseEntity byggResponse(StatusFraArenaForvalterResponse response) {
        StringBuilder status = new StringBuilder();

        if (!CollectionUtils.isEmpty(response.getArbeidsokerList())) {
            status.append("Nye identer opprettet med meldekort: \n")
                    .append(response.getArbeidsokerList().stream().map(arbeidsoker -> arbeidsoker.getPersonident()))
                    .append(". ");
        }

        return ResponseEntity.ok().body(status.toString());
    }

    private List<String> hentGyldigeIdenter(SyntetiserArenaRequest arenaRequest) {
        List<String> levendeIdenter = hodejegerenConsumer.finnLevendeIdenterOverAlder(arenaRequest.getAvspillergruppeId());
        List<String> eksisterendeIdenter = arenaForvalterConsumer.hentEksisterendeIdenter();

        levendeIdenter.removeAll(eksisterendeIdenter);
        int antallNyeIdenter = arenaRequest.getAntallNyeIdenter();

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
