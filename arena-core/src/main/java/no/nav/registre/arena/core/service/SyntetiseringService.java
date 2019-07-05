package no.nav.registre.arena.core.service;


import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.consumer.rs.ArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.HodejegerenConsumer;
import no.nav.registre.arena.core.consumer.rs.responses.Arbeidsoker;
import no.nav.registre.arena.core.consumer.rs.responses.StatusFraArenaForvalterResponse;
import no.nav.registre.arena.core.provider.rs.requests.ArenaSaveInHodejegerenRequest;
import no.nav.registre.arena.core.provider.rs.requests.IdentMedData;
import no.nav.registre.arena.core.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.arena.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static java.lang.Math.floor;

@Service
@Slf4j
public class SyntetiseringService {

    private static final double PROSENTANDEL_SOM_SKAL_HA_MELDEKORT = 0.2;
    private static final String ARENA_FORVALTER_NAME = "arena-forvalter";

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
        lagreArenaBrukereIHodejegeren(nyeBrukere);
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

    public ResponseEntity slettBrukereIArenaForvalter(SyntetiserArenaRequest arenaRequest, String personident) {

        StringBuilder responseBody = new StringBuilder();
        if (arenaForvalterConsumer.slettBrukerSuccessful(arenaRequest.getMiljoe(), personident)) {
            responseBody.append("Personident" + personident + " ble slettet fra Arena Forvalter i miljø " + arenaRequest.getMiljoe() + ".");
        } else {
            responseBody.append("Kunne ikke slette " + personident + " fra Arena Forvalter, miljø " + arenaRequest.getMiljoe() + ".");
        }

        return ResponseEntity.ok().body(responseBody.toString());
    }

    private void lagreArenaBrukereIHodejegeren(NyeBrukereList nyeBrukere) {
        List<IdentMedData> brukereSomSkalLagres = new ArrayList<>();
        for (NyBruker bruker : nyeBrukere.getNyeBrukere()) {
            List<NyBruker> data = new ArrayList<>(Collections.singletonList(bruker));
            brukereSomSkalLagres.add(new IdentMedData(bruker.getPersonident(), data));
        }
        hodejegerenConsumer.saveHistory(new ArenaSaveInHodejegerenRequest(ARENA_FORVALTER_NAME, brukereSomSkalLagres));
    }


    private ResponseEntity byggOpprettedeBrukereResponse(StatusFraArenaForvalterResponse response) {
        StringBuilder status = new StringBuilder();

        if (!CollectionUtils.isEmpty(response.getArbeidsokerList())) {
            status.append("Nye identer opprettet med meldekort: \n")
                    .append(response.getArbeidsokerList().stream().map(Arbeidsoker::getPersonident))
                    .append(". ");
        } else {
            status.append("Fant ingen identer som kunne opprettes i Arena Forvalteren.");
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
