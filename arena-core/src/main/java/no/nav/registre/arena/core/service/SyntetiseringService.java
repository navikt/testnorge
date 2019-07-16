package no.nav.registre.arena.core.service;


import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.consumer.rs.ArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.HodejegerenConsumer;
import no.nav.registre.arena.core.consumer.rs.responses.Arbeidsoker;
import no.nav.registre.arena.core.provider.rs.requests.ArenaSaveInHodejegerenRequest;
import no.nav.registre.arena.core.provider.rs.requests.IdentMedData;
import no.nav.registre.arena.core.provider.rs.requests.SlettArenaRequest;
import no.nav.registre.arena.core.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.arena.domain.NyBruker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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


    public List<Arbeidsoker> sendBrukereTilArenaForvalterConsumer(SyntetiserArenaRequest arenaRequest) {
        if (arenaRequest.getAntallNyeIdenter() == null) {
            int antallBrukereAaOpprette = getAntallBrukereForAaFylleArenaForvalteren(arenaRequest);

            if (antallBrukereAaOpprette > 0)
                arenaRequest.setAntallNyeIdenter(antallBrukereAaOpprette);
            else
                return new ArrayList<>();
        }

        List<String> nyeIdenter = hentGyldigeIdenter(arenaRequest);
        List<NyBruker> nyeBrukere = opprettNyeBrukere(nyeIdenter, arenaRequest.getMiljoe());
        lagreArenaBrukereIHodejegeren(nyeBrukere);

        return arenaForvalterConsumer.sendTilArenaForvalter(nyeBrukere);
    }

    public List<String> slettBrukereIArenaForvalter(SlettArenaRequest arenaRequest) {

        List<String> slettedeIdenter = new ArrayList<>();

        for (String personident : arenaRequest.getIdenter()) {
            if (arenaForvalterConsumer.slettBrukerSuccessful(personident, arenaRequest.getMiljoe())) {
                slettedeIdenter.add(personident);
            }
        }

        return slettedeIdenter;
    }

    private int getAntallBrukereForAaFylleArenaForvalteren(SyntetiserArenaRequest arenaRequest) {
        double levendeIdenter = hodejegerenConsumer.finnLevendeIdenterOverAlder(arenaRequest.getAvspillergruppeId()).size();
        double eksisterendeIdenter = hentEksisterendeIdenter().size();

        return (int) (floor(levendeIdenter * PROSENTANDEL_SOM_SKAL_HA_MELDEKORT) - eksisterendeIdenter);
    }

    private void lagreArenaBrukereIHodejegeren(List<NyBruker> nyeBrukere) {

        List<IdentMedData> brukereSomSkalLagres = new ArrayList<>();

        for (NyBruker bruker : nyeBrukere) {

            List<NyBruker> data = Collections.singletonList(bruker);
            brukereSomSkalLagres.add(new IdentMedData(bruker.getPersonident(), data));

        }
        hodejegerenConsumer.saveHistory(new ArenaSaveInHodejegerenRequest(ARENA_FORVALTER_NAME, brukereSomSkalLagres));
    }

    private List<String> hentGyldigeIdenter(SyntetiserArenaRequest arenaRequest) {
        List<String> levendeIdenter = hodejegerenConsumer.finnLevendeIdenterOverAlder(arenaRequest.getAvspillergruppeId());
        List<String> eksisterendeIdenter = hentEksisterendeIdenter();
        int antallNyeIdenter = arenaRequest.getAntallNyeIdenter();

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

    private List<String> hentEksisterendeIdenter() {

        List<Arbeidsoker> arbeisokere = arenaForvalterConsumer.hentBrukere();

        if (arbeisokere.isEmpty()) {
            log.error("Fant ingen eksisterende identer.");
            return new ArrayList<>();
        }

        return arbeisokere.stream().map(Arbeidsoker::getPersonident).collect(Collectors.toList());
    }

    private List<NyBruker> opprettNyeBrukere(List<String> identerFraHodejegeren, String miljoe) {

        List<NyBruker> nyeBrukere = new ArrayList<>(identerFraHodejegeren.size());

        for (String ident : identerFraHodejegeren) {
            nyeBrukere.add(NyBruker.builder()
                    .personident(ident)
                    .miljoe(miljoe)
                    .kvalifiseringsgruppe("IKVAL")
                    .automatiskInnsendingAvMeldekort(true)
                    .build());
        }

        return nyeBrukere;
    }


}
