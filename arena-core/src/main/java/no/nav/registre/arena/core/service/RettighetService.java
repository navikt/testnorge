package no.nav.registre.arena.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import no.nav.registre.arena.core.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.RettighetSyntConsumer;
import no.nav.registre.arena.core.consumer.rs.responses.rettighet.NyRettighet;
import no.nav.registre.arena.core.consumer.rs.request.RettighetFritakMeldekortRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTvungenForvaltningRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetUngUfoerRequest;
import no.nav.registre.arena.core.consumer.rs.responses.NyeBrukereResponse;
import no.nav.registre.arena.core.consumer.rs.responses.rettighet.ArenaForvalterNyRettighetResponse;
import no.nav.registre.arena.domain.NyBrukerFeil;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@Service
@RequiredArgsConstructor
public class RettighetService {

    private static final int MIN_ALDER_UNG_UFOER = 18;
    private static final int MAX_ALDER_UNG_UFOER = 36;
    private static final String BEGRUNNELSE = "Syntetisert rettighet";

    private final HodejegerenConsumer hodejegerenConsumer;
    private final RettighetSyntConsumer rettighetSyntConsumer;
    private final RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;
    private final SyntetiseringService syntetiseringService;
    private final Random rand;

    public List<ArenaForvalterNyRettighetResponse> genererUngUfoer(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        List<String> utvalgteIdenter = getUtvalgteIdenterIAldersgruppe(avspillergruppeId, antallNyeIdenter);
        List<NyRettighet> syntetiserteRettigheter = rettighetSyntConsumer.syntetiserRettighetUngUfoer(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>();
        for (NyRettighet syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            RettighetUngUfoerRequest rettighetRequest = new RettighetUngUfoerRequest(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgteIdenter.remove(rand.nextInt(utvalgteIdenter.size())));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        return opprettArbeidssoekerOgSendRettigheterTilForvalter(rettigheter, miljoe);
    }

    public List<ArenaForvalterNyRettighetResponse> genererTvungenForvaltning(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        List<String> utvalgteIdenter = getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        List<NyRettighet> syntetiserteRettigheter = rettighetSyntConsumer.syntetiserRettighetTvungenForvaltning(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>();
        for (NyRettighet syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            //            syntetisertRettighet.setGjeldendeKontonr(new ArrayList<>());
            //            syntetisertRettighet.setUtbetalingsadresse(new ArrayList<>());
            RettighetTvungenForvaltningRequest rettighetRequest = new RettighetTvungenForvaltningRequest(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgteIdenter.remove(rand.nextInt(utvalgteIdenter.size())));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        return opprettArbeidssoekerOgSendRettigheterTilForvalter(rettigheter, miljoe);
    }

    public List<ArenaForvalterNyRettighetResponse> genererFritakMeldekort(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        List<String> utvalgteIdenter = getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        List<NyRettighet> syntetiserteRettigheter = rettighetSyntConsumer.syntetiserRettighetFritakMeldekort(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>();
        for (NyRettighet syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            RettighetFritakMeldekortRequest rettighetRequest = new RettighetFritakMeldekortRequest(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgteIdenter.remove(rand.nextInt(utvalgteIdenter.size())));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        return opprettArbeidssoekerOgSendRettigheterTilForvalter(rettigheter, miljoe);
    }

    private List<ArenaForvalterNyRettighetResponse> opprettArbeidssoekerOgSendRettigheterTilForvalter(List<RettighetRequest> rettigheter, String miljoe) {
        List<String> identerIArena = syntetiseringService.hentEksisterendeArbeidsoekerIdenter();
        List<String> uregistrerteBrukere = rettigheter.stream().filter(rettighet -> !identerIArena.contains(rettighet.getPersonident())).map(RettighetRequest::getPersonident)
                .collect(Collectors.toList());

        NyeBrukereResponse nyeBrukereResponse = syntetiseringService.byggArbeidsoekereOgLagreIHodejegeren(uregistrerteBrukere, miljoe);
        List<String> feiledeIdenter = nyeBrukereResponse.getNyBrukerFeilList().stream().map(NyBrukerFeil::getPersonident).collect(Collectors.toList());
        rettigheter.removeIf(rettighet -> feiledeIdenter.contains(rettighet.getPersonident()));

        return rettighetArenaForvalterConsumer.opprettRettighet(rettigheter);
    }

    private List<String> getUtvalgteIdenter(
            Long avspillergruppeId,
            int antallNyeIdenter
    ) {
        List<String> levendeIdenter = hodejegerenConsumer.getLevende(avspillergruppeId);
        Collections.shuffle(levendeIdenter);
        return levendeIdenter.subList(0, antallNyeIdenter);
    }

    private List<String> getUtvalgteIdenterIAldersgruppe(
            Long avspillergruppeId,
            int antallNyeIdenter
    ) {
        List<String> levendeIdenterIAldersgruppe = hodejegerenConsumer.getLevende(avspillergruppeId, MIN_ALDER_UNG_UFOER, MAX_ALDER_UNG_UFOER);
        Collections.shuffle(levendeIdenterIAldersgruppe);
        return levendeIdenterIAldersgruppe.subList(0, antallNyeIdenter);
    }
}
