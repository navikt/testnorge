package no.nav.registre.arena.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.arena.core.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.RettighetSyntConsumer;
import no.nav.registre.arena.core.consumer.rs.request.RettighetAapRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetFritakMeldekortRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTvungenForvaltningRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetUngUfoerRequest;
import no.nav.registre.arena.domain.NyBrukerFeil;
import no.nav.registre.arena.domain.aap.forvalter.Adresse;
import no.nav.registre.arena.domain.aap.forvalter.Forvalter;
import no.nav.registre.arena.domain.aap.forvalter.Konto;
import no.nav.registre.arena.domain.rettighet.NyRettighetResponse;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;

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

    public List<NyRettighetResponse> genererVedtakshistorikk(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        var vedtakshistorikk = rettighetSyntConsumer.syntetiserVedtakshistorikk(utvalgteIdenter.size());

        int antallTvungenForvaltning = vedtakshistorikk.stream().mapToInt(vedtak -> vedtak.getTvungenForvaltning().size()).sum();
        var identerMedKontonummer = getIdenterMedKontoinformasjon(avspillergruppeId, miljoe, antallTvungenForvaltning);

        List<RettighetRequest> rettighetRequester = new ArrayList<>();
        for (var vedtak : vedtakshistorikk) {
            if (!utvalgteIdenter.isEmpty()) {
                String personident = utvalgteIdenter.remove(utvalgteIdenter.size() - 1);

                // aap:
                RettighetRequest rettighetRequest = new RettighetAapRequest(vedtak.getAap());
                rettighetRequest.setPersonident(personident);
                rettighetRequest.setMiljoe(miljoe);
                ((RettighetAapRequest) rettighetRequest).getNyeAap().forEach(rettighet -> rettighet.setBegrunnelse(BEGRUNNELSE));
                rettighetRequester.add(rettighetRequest);

                // ung-ufør:
                rettighetRequest = new RettighetUngUfoerRequest(vedtak.getUngUfoer());
                rettighetRequest.setPersonident(personident);
                rettighetRequest.setMiljoe(miljoe);
                ((RettighetUngUfoerRequest) rettighetRequest).getNyeAaungufor().forEach(rettighet -> rettighet.setBegrunnelse(BEGRUNNELSE));
                rettighetRequester.add(rettighetRequest);

                // tvungen forvaltning:
                rettighetRequest = new RettighetTvungenForvaltningRequest(vedtak.getTvungenForvaltning());
                rettighetRequest.setPersonident(personident);
                rettighetRequest.setMiljoe(miljoe);
                ((RettighetTvungenForvaltningRequest) rettighetRequest).getNyeAatfor().forEach(rettighet -> {
                    rettighet.setForvalter(buildForvalter(identerMedKontonummer.remove(identerMedKontonummer.size() - 1)));
                    rettighet.setBegrunnelse(BEGRUNNELSE);
                });
                rettighetRequester.add(rettighetRequest);

                // fritak meldekort:
                rettighetRequest = new RettighetFritakMeldekortRequest(vedtak.getFritakMeldekort());
                rettighetRequest.setPersonident(personident);
                rettighetRequest.setMiljoe(miljoe);
                ((RettighetFritakMeldekortRequest) rettighetRequest).getNyeFritak().forEach(rettighet -> rettighet.setBegrunnelse(BEGRUNNELSE));
                rettighetRequester.add(rettighetRequest);
            }
        }

        return rettighetArenaForvalterConsumer.opprettRettighet(rettighetRequester);
    }

    public List<NyRettighetResponse> genererAap(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        var syntetiserteRettigheter = rettighetSyntConsumer.syntetiserRettighetAap(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>();
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetAapRequest(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        return opprettArbeidssoekerOgSendRettigheterTilForvalter(rettigheter, miljoe);
    }

    public List<NyRettighetResponse> genererUngUfoer(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = getUtvalgteIdenterIAldersgruppe(avspillergruppeId, antallNyeIdenter);
        var syntetiserteRettigheter = rettighetSyntConsumer.syntetiserRettighetUngUfoer(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>();
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetUngUfoerRequest(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        return opprettArbeidssoekerOgSendRettigheterTilForvalter(rettigheter, miljoe);
    }

    public List<NyRettighetResponse> genererTvungenForvaltning(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var identerMedKontonummer = getIdenterMedKontoinformasjon(avspillergruppeId, miljoe, antallNyeIdenter);
        var utvalgteIdenter = getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        var syntetiserteRettigheter = rettighetSyntConsumer.syntetiserRettighetTvungenForvaltning(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            syntetisertRettighet.setForvalter(buildForvalter(identerMedKontonummer.remove(identerMedKontonummer.size() - 1)));
            var rettighetRequest = new RettighetTvungenForvaltningRequest(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        return opprettArbeidssoekerOgSendRettigheterTilForvalter(rettigheter, miljoe);
    }

    public List<NyRettighetResponse> genererFritakMeldekort(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        var syntetiserteRettigheter = rettighetSyntConsumer.syntetiserRettighetFritakMeldekort(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>();
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetFritakMeldekortRequest(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        return opprettArbeidssoekerOgSendRettigheterTilForvalter(rettigheter, miljoe);
    }

    private List<NyRettighetResponse> opprettArbeidssoekerOgSendRettigheterTilForvalter(
            List<RettighetRequest> rettigheter,
            String miljoe
    ) {
        var identerIArena = syntetiseringService.hentEksisterendeArbeidsoekerIdenter();
        var uregistrerteBrukere = rettigheter.stream().filter(rettighet -> !identerIArena.contains(rettighet.getPersonident())).map(RettighetRequest::getPersonident)
                .collect(Collectors.toList());

        var nyeBrukereResponse = syntetiseringService.byggArbeidsoekereOgLagreIHodejegeren(uregistrerteBrukere, miljoe);
        var feiledeIdenter = nyeBrukereResponse.getNyBrukerFeilList().stream().map(NyBrukerFeil::getPersonident).collect(Collectors.toList());
        rettigheter.removeIf(rettighet -> feiledeIdenter.contains(rettighet.getPersonident()));

        return rettighetArenaForvalterConsumer.opprettRettighet(rettigheter);
    }

    private List<String> getUtvalgteIdenter(
            Long avspillergruppeId,
            int antallNyeIdenter
    ) {
        var levendeIdenter = hodejegerenConsumer.getLevende(avspillergruppeId);
        Collections.shuffle(levendeIdenter);
        return levendeIdenter.subList(0, antallNyeIdenter);
    }

    private List<String> getUtvalgteIdenterIAldersgruppe(
            Long avspillergruppeId,
            int antallNyeIdenter
    ) {
        var levendeIdenterIAldersgruppe = hodejegerenConsumer.getLevende(avspillergruppeId, MIN_ALDER_UNG_UFOER, MAX_ALDER_UNG_UFOER);
        Collections.shuffle(levendeIdenterIAldersgruppe);
        return levendeIdenterIAldersgruppe.subList(0, antallNyeIdenter);
    }

    private List<KontoinfoResponse> getIdenterMedKontoinformasjon(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var identerMedKontonummer = hodejegerenConsumer.getIdenterMedKontonummer(avspillergruppeId, miljoe, antallNyeIdenter, null, null);
        Collections.shuffle(identerMedKontonummer);
        return identerMedKontonummer;
    }

    private Forvalter buildForvalter(KontoinfoResponse identMedKontoinfo) {
        Konto konto = Konto.builder()
                .kontonr(identMedKontoinfo.getKontonummer())
                .build();
        Adresse adresse = Adresse.builder()
                .adresseLinje1(identMedKontoinfo.getAdresseLinje1())
                .adresseLinje2(identMedKontoinfo.getAdresseLinje2())
                .adresseLinje3(identMedKontoinfo.getAdresseLinje3())
                .fodselsnr(identMedKontoinfo.getFnr())
                .landkode(identMedKontoinfo.getLandkode())
                .navn(identMedKontoinfo.getLandkode())
                .postnr(identMedKontoinfo.getPostnr())
                .build();
        return Forvalter.builder()
                .gjeldendeKontonr(konto)
                .utbetalingsadresse(adresse)
                .build();
    }
}
