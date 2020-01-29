package no.nav.registre.arena.core.service;

import static no.nav.registre.arena.core.service.ServiceUtils.BEGRUNNELSE;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import no.nav.registre.arena.core.consumer.rs.RettighetAapArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.AapSyntConsumer;
import no.nav.registre.arena.core.consumer.rs.request.RettighetAap115Request;
import no.nav.registre.arena.core.consumer.rs.request.RettighetAapRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetFritakMeldekortRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTvungenForvaltningRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetUngUfoerRequest;
import no.nav.registre.arena.domain.vedtak.NyttVedtakAap;
import no.nav.registre.arena.domain.vedtak.NyttVedtakResponse;

@Service
@RequiredArgsConstructor
public class RettighetAapService {

    private final AapSyntConsumer aapSyntConsumer;
    private final RettighetAapArenaForvalterConsumer rettighetAapArenaForvalterConsumer;
    private final BrukereService brukereService;
    private final ServiceUtils serviceUtils;

    public List<NyttVedtakResponse> genererVedtakshistorikk(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = serviceUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        var vedtakshistorikk = aapSyntConsumer.syntetiserVedtakshistorikk(utvalgteIdenter.size());

        int antallTvungenForvaltning = vedtakshistorikk.stream().mapToInt(vedtak -> vedtak.getTvungenForvaltning() != null ? vedtak.getTvungenForvaltning().size() : 0).sum();
        var identerMedKontonummer = serviceUtils.getIdenterMedKontoinformasjon(avspillergruppeId, miljoe, antallTvungenForvaltning);

        List<RettighetRequest> rettigheter = new ArrayList<>();
        for (var vedtak : vedtakshistorikk) {
            if (!utvalgteIdenter.isEmpty()) {
                var personident = utvalgteIdenter.remove(utvalgteIdenter.size() - 1);

                RettighetRequest rettighetRequest;
                // aap_115:
                List<NyttVedtakAap> aap115 = vedtak.getAap115();
                if (aap115 != null && !aap115.isEmpty()) {
                    rettighetRequest = new RettighetAap115Request(aap115);
                    rettighetRequest.setPersonident(personident);
                    rettighetRequest.setMiljoe(miljoe);
                    ((RettighetAap115Request) rettighetRequest).getNyeAap115().forEach(rettighet -> rettighet.setBegrunnelse(BEGRUNNELSE));
                    rettigheter.add(rettighetRequest);
                }

                // aap:
                List<NyttVedtakAap> aap = vedtak.getAap();
                if (aap != null && !aap.isEmpty()) {
                    rettighetRequest = new RettighetAapRequest(aap);
                    rettighetRequest.setPersonident(personident);
                    rettighetRequest.setMiljoe(miljoe);
                    ((RettighetAapRequest) rettighetRequest).getNyeAap().forEach(rettighet -> rettighet.setBegrunnelse(BEGRUNNELSE));
                    rettigheter.add(rettighetRequest);
                }

                // ung-ufør:
                List<NyttVedtakAap> ungUfoer = vedtak.getUngUfoer();
                if (ungUfoer != null && !ungUfoer.isEmpty()) {
                    rettighetRequest = new RettighetUngUfoerRequest(ungUfoer);
                    rettighetRequest.setPersonident(personident);
                    rettighetRequest.setMiljoe(miljoe);
                    ((RettighetUngUfoerRequest) rettighetRequest).getNyeAaungufor().forEach(rettighet -> rettighet.setBegrunnelse(BEGRUNNELSE));
                    rettigheter.add(rettighetRequest);
                }

                // tvungen forvaltning:
                List<NyttVedtakAap> tvungenForvaltning = vedtak.getTvungenForvaltning();
                if (tvungenForvaltning != null && !tvungenForvaltning.isEmpty()) {
                    rettighetRequest = new RettighetTvungenForvaltningRequest(tvungenForvaltning);
                    rettighetRequest.setPersonident(personident);
                    rettighetRequest.setMiljoe(miljoe);
                    for (NyttVedtakAap rettighet : ((RettighetTvungenForvaltningRequest) rettighetRequest).getNyeAatfor()) {
                        rettighet.setForvalter(ServiceUtils.buildForvalter(identerMedKontonummer.remove(identerMedKontonummer.size() - 1)));
                        rettighet.setBegrunnelse(BEGRUNNELSE);
                    }
                    rettigheter.add(rettighetRequest);
                }

                // fritak meldekort:
                List<NyttVedtakAap> fritakMeldekort = vedtak.getFritakMeldekort();
                if (fritakMeldekort != null && !fritakMeldekort.isEmpty()) {
                    rettighetRequest = new RettighetFritakMeldekortRequest(fritakMeldekort);
                    rettighetRequest.setPersonident(personident);
                    rettighetRequest.setMiljoe(miljoe);
                    ((RettighetFritakMeldekortRequest) rettighetRequest).getNyeFritak().forEach(rettighet -> rettighet.setBegrunnelse(BEGRUNNELSE));
                    rettigheter.add(rettighetRequest);
                }
            }
        }
        return rettighetAapArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoekerAap(rettigheter, miljoe));
    }

    public List<NyttVedtakResponse> genererAapMedTilhoerende115(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = serviceUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        var syntetiserteRettigheter = aapSyntConsumer.syntetiserRettighetAap(utvalgteIdenter.size());

        List<RettighetRequest> aap115Rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            var ident = utvalgteIdenter.remove(utvalgteIdenter.size() - 1);
            var aap115 = aapSyntConsumer.syntetiserRettighetAap115(syntetisertRettighet.getFraDato().minusDays(1), syntetisertRettighet.getTilDato()).get(0);
            aap115.setBegrunnelse(BEGRUNNELSE);
            var aap115Rettighet = new RettighetAap115Request(Collections.singletonList(aap115));
            aap115Rettighet.setPersonident(ident);
            aap115Rettighet.setMiljoe(miljoe);
            aap115Rettigheter.add(aap115Rettighet);

            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetAapRequest(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(ident);
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        rettighetAapArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoekerAap(aap115Rettigheter, miljoe));
        return rettighetAapArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoekerAap(rettigheter, miljoe));
    }

    public List<NyttVedtakResponse> genererAap115(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = serviceUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        var syntetiserteRettigheter = aapSyntConsumer.syntetiserRettighetAap115(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetAap115Request(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        return rettighetAapArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoekerAap(rettigheter, miljoe));
    }

    public List<NyttVedtakResponse> genererUngUfoer(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = serviceUtils.getUtvalgteIdenterIAldersgruppe(avspillergruppeId, antallNyeIdenter);
        var syntetiserteRettigheter = aapSyntConsumer.syntetiserRettighetUngUfoer(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetUngUfoerRequest(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        return rettighetAapArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoekerAap(rettigheter, miljoe));
    }

    public List<NyttVedtakResponse> genererTvungenForvaltning(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var identerMedKontonummer = serviceUtils.getIdenterMedKontoinformasjon(avspillergruppeId, miljoe, antallNyeIdenter);
        var utvalgteIdenter = serviceUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        var syntetiserteRettigheter = aapSyntConsumer.syntetiserRettighetTvungenForvaltning(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            syntetisertRettighet.setForvalter(ServiceUtils.buildForvalter(identerMedKontonummer.remove(identerMedKontonummer.size() - 1)));
            var rettighetRequest = new RettighetTvungenForvaltningRequest(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        return rettighetAapArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoekerAap(rettigheter, miljoe));
    }

    public List<NyttVedtakResponse> genererFritakMeldekort(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = brukereService.hentEksisterendeArbeidsoekerIdenter();
        var identerIAvspillergruppe = new HashSet<>(serviceUtils.getLevende(avspillergruppeId));
        utvalgteIdenter.retainAll(identerIAvspillergruppe);
        Collections.shuffle(utvalgteIdenter);
        utvalgteIdenter = utvalgteIdenter.subList(0, antallNyeIdenter);
        var syntetiserteRettigheter = aapSyntConsumer.syntetiserRettighetFritakMeldekort(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetFritakMeldekortRequest(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        return rettighetAapArenaForvalterConsumer.opprettRettighet(rettigheter);
    }
}
