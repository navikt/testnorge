package no.nav.registre.arena.core.service;

import static java.lang.Math.floor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import no.nav.registre.arena.core.consumer.rs.AAPNyRettighetSyntetisererenConsumer;
import no.nav.registre.arena.core.consumer.rs.ArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.responses.NyeBrukereResponse;
import no.nav.registre.arena.core.provider.rs.requests.IdentMedData;
import no.nav.registre.arena.domain.Arbeidsoeker;
import no.nav.registre.arena.domain.NyBruker;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@Service
@Slf4j
@RequiredArgsConstructor
public class SyntetiseringService {

    private static final String ARENA_FORVALTER_NAME = "arena-forvalteren";
    private static final double PROSENTANDEL_SOM_SKAL_HA_MELDEKORT = 0.2;
    private static final int MINIMUM_ALDER = 16;
    private static final int MAKSIMUM_ALDER = 67;

    private final HodejegerenConsumer hodejegerenConsumer;
    private final ArenaForvalterConsumer arenaForvalterConsumer;
    private final AAPNyRettighetSyntetisererenConsumer aapConsumer;
    private final Random random;

    public NyeBrukereResponse opprettArbeidsoekere(Integer antallNyeIdenter, Long avspillergruppeId, String miljoe) {
        List<String> levendeIdenter = hentLevendeIdenter(avspillergruppeId);
        List<String> arbeidsoekerIdenter = hentEksisterendeArbeidsoekerIdenter();

        if (antallNyeIdenter == null) {
            int antallArbeidsoekereAaOpprette = getAntallBrukereForAaFylleArenaForvalteren(levendeIdenter.size(), arbeidsoekerIdenter.size());

            if (antallArbeidsoekereAaOpprette > 0) {
                antallNyeIdenter = antallArbeidsoekereAaOpprette;
            } else {
                log.info("{}% av gyldige brukere funnet av hodejegeren er allerede registrert i Arena.",
                        (PROSENTANDEL_SOM_SKAL_HA_MELDEKORT * 100));
                return new NyeBrukereResponse();
            }
        }

        List<String> nyeIdenter = hentKvalifiserteIdenter(antallNyeIdenter, levendeIdenter, arbeidsoekerIdenter);
        return byggArbeidsoekereOgLagreIHodejegeren(nyeIdenter, miljoe);
    }

    public NyeBrukereResponse opprettArbeidssoeker(String ident, Long avspillergruppeId, String miljoe) {
        List<String> levendeIdenter = hentLevendeIdenter(avspillergruppeId);
        List<String> arbeidsoekerIdenter = hentEksisterendeArbeidsoekerIdenter();

        if (arbeidsoekerIdenter.contains(ident)) {
            log.info("Ident {} er allerede registrert som arbeidsøker.", ident);
            NyeBrukereResponse response = new NyeBrukereResponse();
            response.setArbeidsoekerList(arenaForvalterConsumer.hentArbeidsoekere(ident, null, null));
            return response;
        } else if (!levendeIdenter.contains(ident)) {
            log.info("Ident {} kunne ikke bli funnet av Hodejegeren, og kan derfor ikke opprettes i Arena.", ident);
            return new NyeBrukereResponse();
        }

        return byggArbeidsoekereOgLagreIHodejegeren(Collections.singletonList(ident), miljoe);
    }

    public List<String> hentEksisterendeArbeidsoekerIdenter() {
        List<Arbeidsoeker> arbeidsoekere = arenaForvalterConsumer.hentArbeidsoekere(null, null, null);
        return hentIdentListe(arbeidsoekere);
    }

    public NyeBrukereResponse byggArbeidsoekereOgLagreIHodejegeren(List<String> identer, String miljoe) {

        List<NyBruker> nyeBrukere = identer.stream().map(ident ->
                NyBruker.builder()
                        .personident(ident)
                        .miljoe(miljoe)
                        .kvalifiseringsgruppe("IKVAL")
                        .automatiskInnsendingAvMeldekort(true)
                        .build())
                .collect(Collectors.toList());
        lagreArenaBrukereIHodejegeren(nyeBrukere);

        return arenaForvalterConsumer.sendTilArenaForvalter(nyeBrukere);
    }

    private List<String> hentKvalifiserteIdenter(int antallIdenter, List<String> levendeIdenter, List<String> eksisterendeArbeidsoekere) {
        List<String> identerIkkeIArena = new ArrayList<>(levendeIdenter);
        identerIkkeIArena.removeAll(eksisterendeArbeidsoekere);

        if (identerIkkeIArena.size() <= 0) {
            log.info("Alle identer som ble funnet i hodejegeren eksisterer allerede i Arena Forvalter.");
            return new ArrayList<>();
        }

        if (antallIdenter > identerIkkeIArena.size()) {
            antallIdenter = identerIkkeIArena.size();
            log.info("Fant ikke nok ledige identer i avspillergruppe. Lager meldekort på {} nye identer.", antallIdenter);
        }

        List<String> nyeIdenter = new ArrayList<>(antallIdenter);
        for (int i = 0; i < antallIdenter; i++) {
            nyeIdenter.add(identerIkkeIArena.remove(random.nextInt(identerIkkeIArena.size())));
        }
        return nyeIdenter;
    }

    private List<String> hentLevendeIdenter(Long avspillergruppeId) {
        return hodejegerenConsumer.getLevende(avspillergruppeId, MINIMUM_ALDER, MAKSIMUM_ALDER);
    }

    private void lagreArenaBrukereIHodejegeren(List<NyBruker> nyeBrukere) {
        List<IdentMedData> brukereSomSkalLagres = new ArrayList<>();

        for (NyBruker bruker : nyeBrukere) {
            List<NyBruker> data = Collections.singletonList(bruker);
            brukereSomSkalLagres.add(new IdentMedData(bruker.getPersonident(), data));
        }
        hodejegerenConsumer.saveHistory(ARENA_FORVALTER_NAME, brukereSomSkalLagres);
    }

    private List<String> hentIdentListe(List<Arbeidsoeker> arbeidsoekere) {
        if (arbeidsoekere.isEmpty()) {
            log.info("Fant ingen eksisterende identer.");
            return new ArrayList<>();
        }

        return arbeidsoekere.stream().map(Arbeidsoeker::getPersonident).collect(Collectors.toList());
    }

    private int getAntallBrukereForAaFylleArenaForvalteren(int antallLevendeIdenter, int antallEksisterendeIdenter) {
        return (int) (floor(antallLevendeIdenter * PROSENTANDEL_SOM_SKAL_HA_MELDEKORT) - antallEksisterendeIdenter);
    }
}
