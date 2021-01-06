package no.nav.registre.arena.core.service;

import static java.lang.Math.floor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.arena.core.service.util.IdenterUtils;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Arbeidsoeker;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Kvalifiseringsgrupper;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.NyBruker;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import no.nav.registre.arena.core.consumer.rs.BrukereArenaForvalterConsumer;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@Service
@Slf4j
@RequiredArgsConstructor
@DependencyOn("testnorge-hodejegeren")
public class BrukereService {

    private static final double PROSENTANDEL_SOM_SKAL_HA_MELDEKORT = 0.2;
    private static final int MINIMUM_ALDER = 16;
    private static final int MAKSIMUM_ALDER = 67;

    private final HodejegerenConsumer hodejegerenConsumer;
    private final BrukereArenaForvalterConsumer brukereArenaForvalterConsumer;
    private final IdenterUtils identerUtils;
    private final Random random;

    public NyeBrukereResponse opprettArbeidsoekere(
            Integer antallNyeIdenter,
            Long avspillergruppeId,
            String miljoe
    ) {
        var levendeIdenter = hentLevendeIdenter(avspillergruppeId);
        var arbeidsoekerIdenter = hentEksisterendeArbeidsoekerIdenter();

        if (antallNyeIdenter == null) {
            var antallArbeidsoekereAaOpprette = getAntallBrukereForAaFylleArenaForvalteren(levendeIdenter.size(), arbeidsoekerIdenter.size());

            if (antallArbeidsoekereAaOpprette > 0) {
                antallNyeIdenter = antallArbeidsoekereAaOpprette;
            } else {
                log.info("{}% av gyldige brukere funnet av hodejegeren er allerede registrert i Arena.",
                        (PROSENTANDEL_SOM_SKAL_HA_MELDEKORT * 100));
                return new NyeBrukereResponse();
            }
        }

        var nyeIdenter = hentKvalifiserteIdenter(antallNyeIdenter, levendeIdenter, arbeidsoekerIdenter);
        return sendArbeidssoekereTilArenaForvalter(nyeIdenter, miljoe, Kvalifiseringsgrupper.IKVAL);
    }

    public NyeBrukereResponse opprettArbeidssoeker(
            String ident,
            Long avspillergruppeId,
            String miljoe
    ) {
        var levendeIdenter = hentLevendeIdenter(avspillergruppeId);
        var arbeidsoekerIdenter = hentEksisterendeArbeidsoekerIdenter();

        if (arbeidsoekerIdenter.contains(ident)) {
            log.info("Ident {} er allerede registrert som arbeidsøker.", ident.replaceAll("[\r\n]", ""));
            var response = new NyeBrukereResponse();
            response.setArbeidsoekerList(brukereArenaForvalterConsumer.hentArbeidsoekere(ident, null, null));
            return response;
        } else if (!levendeIdenter.contains(ident)) {
            log.info("Ident {} kunne ikke bli funnet av Hodejegeren, og kan derfor ikke opprettes i Arena.", ident.replaceAll("[\r\n]", ""));
            return new NyeBrukereResponse();
        }

        return sendArbeidssoekereTilArenaForvalter(Collections.singletonList(ident), miljoe, Kvalifiseringsgrupper.IKVAL);
    }

    public List<String> hentEksisterendeArbeidsoekerIdenter() {
        var arbeidsoekere = brukereArenaForvalterConsumer.hentArbeidsoekere(null, null, null);
        return hentIdentListe(arbeidsoekere);
    }

    public List<String> hentEksisterendeArbeidsoekerIdenter(String eier, String miljoe) {
        var arbeidsoekere = brukereArenaForvalterConsumer.hentArbeidsoekere(null, eier, miljoe);
        return hentIdentListe(arbeidsoekere);
    }

    public NyeBrukereResponse sendArbeidssoekereTilArenaForvalter(
            List<String> identer,
            String miljoe,
            Kvalifiseringsgrupper kvalifiseringsgruppe
    ) {
        var nyeBrukere = identer.stream().map(ident ->
                NyBruker.builder()
                        .personident(ident)
                        .miljoe(miljoe)
                        .kvalifiseringsgruppe(kvalifiseringsgruppe)
                        .automatiskInnsendingAvMeldekort(true)
                        .build())
                .collect(Collectors.toList());

        return brukereArenaForvalterConsumer.sendTilArenaForvalter(nyeBrukere);
    }

    private List<String> hentKvalifiserteIdenter(
            int antallIdenter,
            List<String> levendeIdenter,
            List<String> eksisterendeArbeidsoekere
    ) {
        var identerIkkeIArena = new ArrayList<>(levendeIdenter);
        identerIkkeIArena.removeAll(eksisterendeArbeidsoekere);

        if (identerIkkeIArena.isEmpty()) {
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

    private List<String> hentLevendeIdenter(
            Long avspillergruppeId
    ) {
        return hodejegerenConsumer.getLevende(avspillergruppeId, MINIMUM_ALDER, MAKSIMUM_ALDER);
    }

    private List<String> hentIdentListe(
            List<Arbeidsoeker> arbeidsoekere
    ) {
        if (arbeidsoekere.isEmpty()) {
            log.info("Fant ingen eksisterende identer.");
            return new ArrayList<>();
        }

        return arbeidsoekere.stream().map(Arbeidsoeker::getPersonident).collect(Collectors.toList());
    }

    private int getAntallBrukereForAaFylleArenaForvalteren(
            int antallLevendeIdenter,
            int antallEksisterendeIdenter
    ) {
        return (int) (floor(antallLevendeIdenter * PROSENTANDEL_SOM_SKAL_HA_MELDEKORT) - antallEksisterendeIdenter);
    }

    public void opprettArbeidssoekereUtenVedtak(
            int antallIdenter,
            Long avspillergruppeId,
            String miljoe
    ){
        var identer = identerUtils.getUtvalgteIdenterIAldersgruppe(avspillergruppeId, antallIdenter, MINIMUM_ALDER, MAKSIMUM_ALDER, miljoe);

        for(var ident: identer){
            opprettArbeidssoekerUtenVedtak(ident, avspillergruppeId, miljoe);
        }
    }

    public void opprettArbeidssoekerUtenVedtak(
            String ident,
            long avspillergruppeId,
            String miljoe
    ){

    }
}
