package no.nav.registre.arena.core.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.consumer.rs.ArenaForvalterConsumer;
import no.nav.registre.arena.core.utils.Partition;
import no.nav.registre.arena.domain.Arbeidsoeker;
import no.nav.registre.arena.core.provider.rs.requests.IdentMedData;
import no.nav.registre.arena.domain.NyBruker;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import static java.lang.Math.floor;

@Service
@Slf4j
@RequiredArgsConstructor
public class SyntetiseringService {

    private static final String ARENA_FORVALTER_NAME = "arena-forvalteren";
    private static final double PROSENTANDEL_SOM_SKAL_HA_MELDEKORT = 0.2;
    private static final int MAX_FORESPOERSEL_STOERRELSE = 50;
    private int MINIMUM_ALDER = 16;

    private final HodejegerenConsumer hodejegerenConsumer;
    private final ArenaForvalterConsumer arenaForvalterConsumer;
    private final Random random;


    public List<Arbeidsoeker> opprettArbeidsoekere(Integer antallNyeIdenter, Long avspillergruppeId, String miljoe) {

        List<String> ledigeIdenter = hentLedigeIdenter(antallNyeIdenter, avspillergruppeId, miljoe);
        antallNyeIdenter = ledigeIdenter.size();

        if (ledigeIdenter.size() <= 0) {
            log.info("Alle identer funnet av hodejegeren er aktive i Arena Forvalteren. Ingen nye identer ble lagt til.");
            return new ArrayList<>();
        }

        log.info("Registrerer {} nye identer som aktive i Arena Forvalteren.", antallNyeIdenter);
        return byggArbeidsoekereOgLagreIHodejegeren(ledigeIdenter, miljoe);
    }

    private List<String> hentLedigeIdenter(Integer antallNyeIdenter, Long avspillergruppeId, String miljoe) {

        List<String> levendeIdenter = hentLevendeIdenter(avspillergruppeId);

        if (Objects.isNull(antallNyeIdenter)) {
            log.info("Å opprette {}% identer er foreløpig foreldet grunnet tregheter i systemet.",
                    (PROSENTANDEL_SOM_SKAL_HA_MELDEKORT * 100));
            return new ArrayList<>();
        }

        List<String> nyeIdenter = new ArrayList<>(antallNyeIdenter);
        List<String> forespoerselIdenter = new ArrayList<>(MAX_FORESPOERSEL_STOERRELSE);

        while (nyeIdenter.size() < antallNyeIdenter) {
            if (levendeIdenter.size() <= 0) {
                log.info("Ingen flere kvalifiserte identer. Oppretter {} nye identer i Arena Forvalteren.", nyeIdenter.size());
                return nyeIdenter;
            }

            for (int i = 0; i < MAX_FORESPOERSEL_STOERRELSE && levendeIdenter.size() > 0; i++) {
                forespoerselIdenter.add(levendeIdenter.remove(random.nextInt(levendeIdenter.size())));
            }

            // TODO: SPLIT IN THREADS
            List<String> aktiveIdenter = hentAtiveIdenter(forespoerselIdenter,  miljoe);
            forespoerselIdenter.removeAll(aktiveIdenter);
            nyeIdenter.addAll(forespoerselIdenter);
        }

        return nyeIdenter;
    }

    // Mulig man må sjekke om identene er aktive i Arena også. Dersom de blir endret direkte der uten at denne endringen
    // har gått gjennom Arena Forvalteren, blir ikke dette gjenspeilet i den.
    private List<String> hentAtiveIdenter(List<String> levendeIdenter, String miljoe) {
        List<List<String>> parter = Partition.ofSize(levendeIdenter, 50);
        List<String> aktiveIdenter = new ArrayList<>(levendeIdenter.size());

        for (List<String> part : parter) {
            aktiveIdenter.addAll(arenaForvalterConsumer.arbeidsoekerStatusAktiv(part, miljoe)
                    .entrySet().stream()
                    .filter(s -> s.getValue().equals(true))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList()));
        }

        return aktiveIdenter;
    }

    public List<Arbeidsoeker> opprettArbeidssoeker(String ident, Long avspillergruppeId, String miljoe) {
        List<String> levendeIdenter = hentLevendeIdenter(avspillergruppeId);

        List<String> arbeidsoekerIdenter = hentEksisterendeArbeidsoekerIdenter();

        if (arbeidsoekerIdenter.contains(ident)) {
            log.info("Ident {} er allerede registrert som arbeidsøker.", ident);
            return arenaForvalterConsumer.hentArbeidsoekere(ident, null, null);
        } else if (!levendeIdenter.contains(ident)) {
            log.info("Ident {} kunne ikke bli funnet av Hodejegeren, og kan derfor ikke opprettes i Arena.", ident);
            return new ArrayList<>();
        }

        return byggArbeidsoekereOgLagreIHodejegeren(Collections.singletonList(ident), miljoe);
    }


    private List<String> hentLevendeIdenter(Long avspillergruppeId) {
        return hodejegerenConsumer.getLevende(avspillergruppeId, MINIMUM_ALDER);
    }


    private List<Arbeidsoeker> byggArbeidsoekereOgLagreIHodejegeren(List<String> identer, String miljoe) {
        List<NyBruker> nyeBrukere = identer.stream().map(ident -> NyBruker.builder()
                .personident(ident)
                .miljoe(miljoe)
                .kvalifiseringsgruppe("IKVAL")
                .automatiskInnsendingAvMeldekort(true)
                .build()).collect(Collectors.toList());
        lagreArenaBrukereIHodejegeren(nyeBrukere);

        return arenaForvalterConsumer.sendTilArenaForvalter(nyeBrukere);
    }


    private void lagreArenaBrukereIHodejegeren(List<NyBruker> nyeBrukere) {

        List<IdentMedData> brukereSomSkalLagres = new ArrayList<>();

        for (NyBruker bruker : nyeBrukere) {

            List<NyBruker> data = Collections.singletonList(bruker);
            brukereSomSkalLagres.add(new IdentMedData(bruker.getPersonident(), data));

        }
        hodejegerenConsumer.saveHistory(ARENA_FORVALTER_NAME, brukereSomSkalLagres);
    }


    private List<String> hentEksisterendeArbeidsoekerIdenter() {
        List<Arbeidsoeker> arbeidsoekere = arenaForvalterConsumer.hentArbeidsoekere(null, null, null);
        return hentIdentListe(arbeidsoekere);
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
