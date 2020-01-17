package no.nav.registre.arena.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;
import no.nav.registre.arena.domain.NyBrukerFeil;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;
import no.nav.registre.testnorge.consumers.hodejegeren.response.RelasjonsResponse;

@Service
@RequiredArgsConstructor
public class ServiceUtils {

    private static final int MIN_ALDER_UNG_UFOER = 18;
    private static final int MAX_ALDER_UNG_UFOER = 36;
    private static final String KVALIFISERINGSGRUPPE_IKVAL = "IKVAL";
    private static final String KVALIFISERINGSGRUPPE_BFORM = "BFORM";
    private static final String KVALIFISERINGSGRUPPE_BATT = "BATT";
    private static final String KVALIFISERINGSGRUPPE_VARIG = "VARIG";
    public static final String BEGRUNNELSE = "Syntetisert rettighet";

    private final HodejegerenConsumer hodejegerenConsumer;
    private final SyntetiseringService syntetiseringService;
    private final Random rand;

    public List<String> getLevende(Long avspillergruppeId) {
        return hodejegerenConsumer.getLevende(avspillergruppeId);
    }

    public List<String> getUtvalgteIdenter(
            Long avspillergruppeId,
            int antallNyeIdenter
    ) {
        var levendeIdenter = hodejegerenConsumer.getLevende(avspillergruppeId);
        Collections.shuffle(levendeIdenter);
        return levendeIdenter.subList(0, antallNyeIdenter);
    }

    public List<String> getUtvalgteIdenterIAldersgruppe(
            Long avspillergruppeId,
            int antallNyeIdenter
    ) {
        var levendeIdenterIAldersgruppe = hodejegerenConsumer.getLevende(avspillergruppeId, MIN_ALDER_UNG_UFOER, MAX_ALDER_UNG_UFOER);
        Collections.shuffle(levendeIdenterIAldersgruppe);
        return levendeIdenterIAldersgruppe.subList(0, antallNyeIdenter);
    }

    public List<KontoinfoResponse> getIdenterMedKontoinformasjon(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        if (antallNyeIdenter == 0) {
            return new ArrayList<>();
        }
        var identerMedKontonummer = hodejegerenConsumer.getIdenterMedKontonummer(avspillergruppeId, miljoe, antallNyeIdenter, null, null);
        Collections.shuffle(identerMedKontonummer);
        return identerMedKontonummer;
    }

    public List<RettighetRequest> opprettArbeidssoeker(
            List<RettighetRequest> rettigheter,
            String miljoe
    ) {
        var identerIArena = syntetiseringService.hentEksisterendeArbeidsoekerIdenter();
        var uregistrerteBrukere = rettigheter.stream().filter(rettighet -> !identerIArena.contains(rettighet.getPersonident())).map(RettighetRequest::getPersonident)
                .collect(Collectors.toSet());

        if (!uregistrerteBrukere.isEmpty()) {
            var nyeBrukereResponse = syntetiseringService
                    .byggArbeidsoekereOgLagreIHodejegeren(new ArrayList<>(uregistrerteBrukere), miljoe, rand.nextBoolean() ? KVALIFISERINGSGRUPPE_BATT : KVALIFISERINGSGRUPPE_VARIG);
            var feiledeIdenter = nyeBrukereResponse.getNyBrukerFeilList().stream().map(NyBrukerFeil::getPersonident).collect(Collectors.toCollection(ArrayList::new));
            rettigheter.removeIf(rettighet -> feiledeIdenter.contains(rettighet.getPersonident()));
        }
        return rettigheter;
    }

    public List<String> getIdenterMedFoedselsmelding(Long avspillergruppeId) {
        return hodejegerenConsumer.getFoedte(avspillergruppeId, null, null);
    }

    public RelasjonsResponse getRelasjonerTilIdent(
            String ident,
            String miljoe
    ) {
        return hodejegerenConsumer.getRelasjoner(ident, miljoe);
    }
}
