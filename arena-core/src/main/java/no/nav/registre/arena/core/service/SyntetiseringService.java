package no.nav.registre.arena.core.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.consumer.rs.ArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.responses.Arbeidsoker;
import no.nav.registre.arena.core.provider.rs.requests.IdentMedData;
import no.nav.registre.arena.domain.NyBruker;
import no.nav.registre.testnorge.consumers.HodejegerenConsumer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static java.lang.Math.floor;

@Service
@Slf4j
@RequiredArgsConstructor
public class SyntetiseringService {

    private static final String ARENA_FORVALTER_NAME = "arena-forvalteren";
    private static final double PROSENTANDEL_SOM_SKAL_HA_MELDEKORT = 0.2;
    private int MINIMUM_ALDER = 16;

    private final HodejegerenConsumer hodejegerenConsumer;
    private final ArenaForvalterConsumer arenaForvalterConsumer;
    private final Random random;


    public List<Arbeidsoker> sendBrukereTilArenaForvalterConsumer(Integer antallNyeIdenter, Long avspillergruppeId, String miljoe) {
        List<String> levendeIdenter = hodejegerenConsumer.getLevende(avspillergruppeId, MINIMUM_ALDER);
        List<Arbeidsoker> eksisterendeArbeidsokere = arenaForvalterConsumer.hentBrukere();

        if (antallNyeIdenter == null) {
            int antallBrukereAaOpprette = getAntallBrukereForAaFylleArenaForvalteren(levendeIdenter.size(),
                    hentIdentListe(eksisterendeArbeidsokere).size());

            if (antallBrukereAaOpprette > 0)
                antallNyeIdenter = antallBrukereAaOpprette;
            else {
                log.info("{}% av gyldige brukere funnet av hodejegeren er allerede registrert i Arena Forvalteren.",
                        (PROSENTANDEL_SOM_SKAL_HA_MELDEKORT * 100));
                return new ArrayList<>();
            }
        }

        List<String> nyeIdenter = hentGyldigeIdenter(antallNyeIdenter, levendeIdenter, eksisterendeArbeidsokere);
        List<NyBruker> nyeBrukere = nyeIdenter.stream().map(ident -> NyBruker.builder()
                .personident(ident)
                .miljoe(miljoe)
                .kvalifiseringsgruppe("IKVAL")
                .automatiskInnsendingAvMeldekort(true)
                .build()).collect(Collectors.toList());

        lagreArenaBrukereIHodejegeren(nyeBrukere);

        return arenaForvalterConsumer.sendTilArenaForvalter(nyeBrukere);
    }

    public List<String> slettBrukereIArenaForvalter(List<String> identerToDelete, String miljoe) {

        List<String> slettedeIdenter = new ArrayList<>();

        for (String personident : identerToDelete) {
            if (arenaForvalterConsumer.slettBrukerSuccessful(personident, miljoe)) {
                slettedeIdenter.add(personident);
            }
        }

        return slettedeIdenter;
    }

    private int getAntallBrukereForAaFylleArenaForvalteren(int antallLevendeIdenter, int antallEksisterendeIdenter) {
        return (int) (floor(antallLevendeIdenter * PROSENTANDEL_SOM_SKAL_HA_MELDEKORT) - antallEksisterendeIdenter);
    }

    private void lagreArenaBrukereIHodejegeren(List<NyBruker> nyeBrukere) {

        List<IdentMedData> brukereSomSkalLagres = new ArrayList<>();

        for (NyBruker bruker : nyeBrukere) {

            List<NyBruker> data = Collections.singletonList(bruker);
            brukereSomSkalLagres.add(new IdentMedData(bruker.getPersonident(), data));

        }
        hodejegerenConsumer.saveHistory(ARENA_FORVALTER_NAME, brukereSomSkalLagres);
    }

    private List<String> hentGyldigeIdenter(int antallNyeIdenter, List<String> levendeIdenter, List<Arbeidsoker> eksisterendeArbeidsokere) {
        List<String> eksisterendeIdenter = hentIdentListe(eksisterendeArbeidsokere);

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

    private List<String> hentIdentListe(List<Arbeidsoker> arbeidsokere) {

        if (arbeidsokere.isEmpty()) {
            log.error("Fant ingen eksisterende identer.");
            return new ArrayList<>();
        }

        return arbeidsokere.stream().map(Arbeidsoker::getPersonident).collect(Collectors.toList());
    }
}
