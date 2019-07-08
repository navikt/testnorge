package no.nav.registre.arena.core.service;


import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.consumer.rs.ArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.HodejegerenConsumer;
import no.nav.registre.arena.core.consumer.rs.responses.StatusFraArenaForvalterResponse;
import no.nav.registre.arena.core.provider.rs.requests.ArenaSaveInHodejegerenRequest;
import no.nav.registre.arena.core.provider.rs.requests.IdentMedData;
import no.nav.registre.arena.core.provider.rs.requests.SlettArenaRequest;
import no.nav.registre.arena.core.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.arena.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.lang.Math.floor;

@Service
@Slf4j
public class SyntetiseringService {

    private static final String ARENA_FORVALTER_NAME = "arena-forvalteren";
    private static final double PROSENTANDEL_SOM_SKAL_HA_MELDEKORT = 0.2;


    @Autowired
    HodejegerenConsumer hodejegerenConsumer;
    @Autowired
    ArenaForvalterConsumer arenaForvalterConsumer;
    @Autowired
    Random random;


    public StatusFraArenaForvalterResponse sendBrukereTilArenaForvalterConsumer(SyntetiserArenaRequest arenaRequest, int antallNyeBrukere) {
        List<String> nyeIdenter = hentGyldigeIdenter(arenaRequest, antallNyeBrukere);
        NyeBrukereList nyeBrukere = opprettNyeBrukere(nyeIdenter, arenaRequest.getMiljoe());
        lagreArenaBrukereIHodejegeren(nyeBrukere);

        return arenaForvalterConsumer.sendTilArenaForvalter(nyeBrukere);
    }

    public List<String> slettBrukereIArenaForvalter(SlettArenaRequest arenaRequest) {

        List<String> slettedeIdenter = new ArrayList<>();

        for (String personident : arenaRequest.getIdenter()) {
            if (arenaForvalterConsumer.slettBrukerSuccessful(arenaRequest.getMiljoe(), personident)) {
                slettedeIdenter.add(personident);
            }
        }

        return slettedeIdenter;
    }

    public int getAntallBrukereForAaFylleArenaForvalteren(SyntetiserArenaRequest arenaRequest) {
        double levendeIdenter = hodejegerenConsumer.finnLevendeIdenterOverAlder(arenaRequest.getAvspillergruppeId()).size();
        double eksisterendeIdenter = arenaForvalterConsumer.hentEksisterendeIdenter().size();

        return (int) (floor(levendeIdenter * PROSENTANDEL_SOM_SKAL_HA_MELDEKORT) - eksisterendeIdenter);
    }

    public double getProsentandelSomSkalHaMeldekort() {
        return PROSENTANDEL_SOM_SKAL_HA_MELDEKORT;
    }

    private void lagreArenaBrukereIHodejegeren(NyeBrukereList nyeBrukere) {

        List<IdentMedData> brukereSomSkalLagres = new ArrayList<>();

        for (NyBruker bruker : nyeBrukere.getNyeBrukere()) {

            List<NyBruker> data = Collections.singletonList(bruker);
            brukereSomSkalLagres.add(new IdentMedData(bruker.getPersonident(), data));

        }
        hodejegerenConsumer.saveHistory(new ArenaSaveInHodejegerenRequest(ARENA_FORVALTER_NAME, brukereSomSkalLagres));
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

        for (int i = 0; i < antallNyeIdenter; i++) {
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
