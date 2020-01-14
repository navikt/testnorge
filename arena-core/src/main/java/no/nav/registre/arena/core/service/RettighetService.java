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
import no.nav.registre.arena.core.consumer.rs.request.RettighetAap115Request;
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
import no.nav.registre.arena.domain.rettighet.NyttVedtak;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;

@Service
@RequiredArgsConstructor
public class RettighetService {

    private static final int MIN_ALDER_UNG_UFOER = 18;
    private static final int MAX_ALDER_UNG_UFOER = 36;
    private static final String BEGRUNNELSE = "Syntetisert rettighet";
    private static final String KVALIFISERINGSGRUPPE_IKVAL = "IKVAL";
    private static final String KVALIFISERINGSGRUPPE_BFORM = "BFORM";
    private static final String KVALIFISERINGSGRUPPE_BATT = "BATT";
    private static final String KVALIFISERINGSGRUPPE_VARIG = "VARIG";

    private final HodejegerenConsumer hodejegerenConsumer;
    private final RettighetSyntConsumer rettighetSyntConsumer;
    private final RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;
    private final SyntetiseringService syntetiseringService;
    private final Random rand;

    public List<NyRettighetResponse> genererVedtakshistorikk(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        var vedtakshistorikk = rettighetSyntConsumer.syntetiserVedtakshistorikk(utvalgteIdenter.size());

        int antallTvungenForvaltning = vedtakshistorikk.stream().mapToInt(vedtak -> vedtak.getTvungenForvaltning() != null ? vedtak.getTvungenForvaltning().size() : 0).sum();
        var identerMedKontonummer = getIdenterMedKontoinformasjon(avspillergruppeId, miljoe, antallTvungenForvaltning);

        List<RettighetRequest> rettigheter = new ArrayList<>();
        for (var vedtak : vedtakshistorikk) {
            if (!utvalgteIdenter.isEmpty()) {
                var personident = utvalgteIdenter.remove(utvalgteIdenter.size() - 1);

                RettighetRequest rettighetRequest;
                // aap:
                List<NyttVedtak> aap = vedtak.getAap();
                if (aap != null && !aap.isEmpty()) {
                    rettighetRequest = new RettighetAapRequest(aap);
                    rettighetRequest.setPersonident(personident);
                    rettighetRequest.setMiljoe(miljoe);
                    ((RettighetAapRequest) rettighetRequest).getNyeAap().forEach(rettighet -> rettighet.setBegrunnelse(BEGRUNNELSE));
                    rettigheter.add(rettighetRequest);
                }

                // aap_115:
                List<NyttVedtak> aap115 = vedtak.getAap115();
                if (aap115 != null && !aap115.isEmpty()) {
                    rettighetRequest = new RettighetAap115Request(aap115);
                    rettighetRequest.setPersonident(personident);
                    rettighetRequest.setMiljoe(miljoe);
                    ((RettighetAap115Request) rettighetRequest).getNyeAap115().forEach(rettighet -> rettighet.setBegrunnelse(BEGRUNNELSE));
                    rettigheter.add(rettighetRequest);
                }

                // ung-ufør:
                List<NyttVedtak> ungUfoer = vedtak.getUngUfoer();
                if (ungUfoer != null && !ungUfoer.isEmpty()) {
                    rettighetRequest = new RettighetUngUfoerRequest(ungUfoer);
                    rettighetRequest.setPersonident(personident);
                    rettighetRequest.setMiljoe(miljoe);
                    ((RettighetUngUfoerRequest) rettighetRequest).getNyeAaungufor().forEach(rettighet -> rettighet.setBegrunnelse(BEGRUNNELSE));
                    rettigheter.add(rettighetRequest);
                }

                // tvungen forvaltning:
                List<NyttVedtak> tvungenForvaltning = vedtak.getTvungenForvaltning();
                if (tvungenForvaltning != null && !tvungenForvaltning.isEmpty()) {
                    rettighetRequest = new RettighetTvungenForvaltningRequest(tvungenForvaltning);
                    rettighetRequest.setPersonident(personident);
                    rettighetRequest.setMiljoe(miljoe);
                    ((RettighetTvungenForvaltningRequest) rettighetRequest).getNyeAatfor().forEach(rettighet -> {
                        rettighet.setForvalter(buildForvalter(identerMedKontonummer.remove(identerMedKontonummer.size() - 1)));
                        rettighet.setBegrunnelse(BEGRUNNELSE);
                    });
                    rettigheter.add(rettighetRequest);
                }

                // fritak meldekort:
                List<NyttVedtak> fritakMeldekort = vedtak.getFritakMeldekort();
                if (fritakMeldekort != null && !fritakMeldekort.isEmpty()) {
                    rettighetRequest = new RettighetFritakMeldekortRequest(fritakMeldekort);
                    rettighetRequest.setPersonident(personident);
                    rettighetRequest.setMiljoe(miljoe);
                    ((RettighetFritakMeldekortRequest) rettighetRequest).getNyeFritak().forEach(rettighet -> rettighet.setBegrunnelse(BEGRUNNELSE));
                    rettigheter.add(rettighetRequest);
                }
            }
        }
        return rettighetArenaForvalterConsumer.opprettRettighet(opprettArbeidssoeker(rettigheter, miljoe));
    }

    public List<NyRettighetResponse> genererAap(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        var syntetiserteRettigheter = rettighetSyntConsumer.syntetiserRettighetAap(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetAapRequest(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        return rettighetArenaForvalterConsumer.opprettRettighet(opprettArbeidssoeker(rettigheter, miljoe));
    }

    public List<NyRettighetResponse> genererAap115(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = getUtvalgteIdenterIAldersgruppe(avspillergruppeId, antallNyeIdenter);
        var syntetiserteRettigheter = rettighetSyntConsumer.syntetiserRettighetAap115(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetAap115Request(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        return rettighetArenaForvalterConsumer.opprettRettighet(opprettArbeidssoeker(rettigheter, miljoe));
    }

    public List<NyRettighetResponse> genererUngUfoer(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = getUtvalgteIdenterIAldersgruppe(avspillergruppeId, antallNyeIdenter);
        var syntetiserteRettigheter = rettighetSyntConsumer.syntetiserRettighetUngUfoer(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetUngUfoerRequest(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        return rettighetArenaForvalterConsumer.opprettRettighet(opprettArbeidssoeker(rettigheter, miljoe));
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

        return rettighetArenaForvalterConsumer.opprettRettighet(opprettArbeidssoeker(rettigheter, miljoe));
    }

    public List<NyRettighetResponse> genererFritakMeldekort(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        var syntetiserteRettigheter = rettighetSyntConsumer.syntetiserRettighetFritakMeldekort(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetFritakMeldekortRequest(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        return rettighetArenaForvalterConsumer.opprettRettighet(opprettArbeidssoeker(rettigheter, miljoe));
    }

    private List<RettighetRequest> opprettArbeidssoeker(
            List<RettighetRequest> rettigheter,
            String miljoe
    ) {
        var identerIArena = syntetiseringService.hentEksisterendeArbeidsoekerIdenter();
        var uregistrerteBrukere = rettigheter.stream().filter(rettighet -> !identerIArena.contains(rettighet.getPersonident())).map(RettighetRequest::getPersonident)
                .collect(Collectors.toList());

        if (!uregistrerteBrukere.isEmpty()) {
            var nyeBrukereResponse = syntetiseringService.byggArbeidsoekereOgLagreIHodejegeren(uregistrerteBrukere, miljoe, rand.nextBoolean() ? KVALIFISERINGSGRUPPE_BATT : KVALIFISERINGSGRUPPE_VARIG);
            var feiledeIdenter = nyeBrukereResponse.getNyBrukerFeilList().stream().map(NyBrukerFeil::getPersonident).collect(Collectors.toCollection(ArrayList::new));
            rettigheter.removeIf(rettighet -> feiledeIdenter.contains(rettighet.getPersonident()));
        }
        return rettigheter;
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
        if (antallNyeIdenter == 0) {
            return new ArrayList<>();
        }
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
