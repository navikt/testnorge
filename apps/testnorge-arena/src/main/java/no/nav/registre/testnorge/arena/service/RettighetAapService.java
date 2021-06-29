package no.nav.registre.testnorge.arena.service;

import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.AKTIVITETSFASE_SYKEPENGEERSTATNING;
import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.BEGRUNNELSE;
import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.MAX_ALDER_AAP;
import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.MAX_ALDER_UNG_UFOER;
import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.MIN_ALDER_AAP;
import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.MIN_ALDER_UNG_UFOER;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.util.ConsumerUtils;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import no.nav.registre.testnorge.arena.consumer.rs.AapSyntConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetAap115Request;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetAapRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetFritakMeldekortRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetTvungenForvaltningRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetUngUfoerRequest;
import no.nav.registre.testnorge.arena.consumer.rs.PensjonTestdataFacadeConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.request.pensjon.PensjonTestdataInntekt;
import no.nav.registre.testnorge.arena.consumer.rs.request.pensjon.PensjonTestdataPerson;
import no.nav.registre.testnorge.arena.service.util.ServiceUtils;
import no.nav.registre.testnorge.arena.service.util.DatoUtils;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.libs.core.util.IdentUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class RettighetAapService {


    private final AapSyntConsumer aapSyntConsumer;
    private final ArbeidssoekerService arbeidssoekerService;
    private final PensjonService pensjonService;
    private final IdentService identService;
    private final DatoUtils datoUtils;
    private final ServiceUtils serviceUtils;
    private final ConsumerUtils consumerUtils;
    private final RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;


    public static final int SYKEPENGEERSTATNING_MAKS_PERIODE = 6;
    public static final LocalDate ARENA_AAP_UNG_UFOER_DATE_LIMIT = LocalDate.of(2020, 1, 31);

    public Map<String, List<NyttVedtakResponse>> genererAapMedTilhoerende115(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var syntRequest = consumerUtils.createSyntRequest(antallNyeIdenter);
        var syntetiserteRettigheter = aapSyntConsumer.syntetiserRettighetAap(syntRequest);

        var utvalgteIdenter = identService.getUtvalgteIdenterIAldersgruppe(avspillergruppeId, antallNyeIdenter, MIN_ALDER_AAP, MAX_ALDER_AAP - 1, miljoe, datoUtils.finnTidligsteDatoAap(syntetiserteRettigheter));

        List<RettighetRequest> aap115Rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            var ident = utvalgteIdenter.remove(utvalgteIdenter.size() - 1);

            var poppStatus = pensjonService.opprettetPersonOgInntektIPopp(ident, miljoe, syntetisertRettighet.getFraDato());
            if (!poppStatus) {
                return Collections.emptyMap();
            }

            arbeidssoekerService.opprettArbeidssoekerAap(ident, miljoe, syntetisertRettighet.getFraDato());

            var aap115Rettighet = getAap115RettighetRequest(syntetisertRettighet.getFraDato().minusDays(1), syntetisertRettighet.getTilDato(), ident, miljoe);
            aap115Rettigheter.add(aap115Rettighet);

            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetAapRequest(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(ident);
            rettighetRequest.setMiljoe(miljoe);

            rettighetRequest.getNyeAap().forEach(rettighet -> {
                if (AKTIVITETSFASE_SYKEPENGEERSTATNING.equals(rettighet.getAktivitetsfase())) {
                    datoUtils.setDatoPeriodeVedtakInnenforMaxAntallMaaneder(rettighet, SYKEPENGEERSTATNING_MAKS_PERIODE);
                }
            });

            rettigheter.add(rettighetRequest);
        }

        rettighetArenaForvalterConsumer.opprettRettighet(aap115Rettigheter);
        var identerMedOpprettedeRettigheter = rettighetArenaForvalterConsumer.opprettRettighet(rettigheter);

        serviceUtils.lagreIHodejegeren(identerMedOpprettedeRettigheter);

        return identerMedOpprettedeRettigheter;
    }

    public Map<String, List<NyttVedtakResponse>> genererAap(
            String ident,
            String miljoe
    ) {
        var syntRequestAap = consumerUtils.createSyntRequest(1);
        var syntetisertRettighet = aapSyntConsumer.syntetiserRettighetAap(syntRequestAap).get(0);

        var poppStatus = pensjonService.opprettetPersonOgInntektIPopp(ident, miljoe, syntetisertRettighet.getFraDato());
        if (!poppStatus) {
            return Collections.emptyMap();
        }

        syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
        var rettighetRequest = new RettighetAapRequest(Collections.singletonList(syntetisertRettighet));
        rettighetRequest.setPersonident(ident);
        rettighetRequest.setMiljoe(miljoe);

        if (AKTIVITETSFASE_SYKEPENGEERSTATNING.equals(rettighetRequest.getNyeAap().get(0).getAktivitetsfase())) {
            datoUtils.setDatoPeriodeVedtakInnenforMaxAntallMaaneder(rettighetRequest.getNyeAap().get(0), SYKEPENGEERSTATNING_MAKS_PERIODE);
        }

        arbeidssoekerService.opprettArbeidssoekerAap(ident, miljoe, syntetisertRettighet.getFraDato());

        return rettighetArenaForvalterConsumer.opprettRettighet(Collections.singletonList(rettighetRequest));
    }

    private RettighetRequest getAap115RettighetRequest(
            LocalDate fraDato,
            LocalDate tilDato,
            String ident,
            String miljoe
    ) {
        var syntRequest115 = consumerUtils.createSyntRequest(fraDato, tilDato);
        var aap115 = aapSyntConsumer.syntetiserRettighetAap115(syntRequest115).get(0);
        aap115.setBegrunnelse(BEGRUNNELSE);
        var aap115Rettighet = new RettighetAap115Request(Collections.singletonList(aap115));
        aap115Rettighet.setPersonident(ident);
        aap115Rettighet.setMiljoe(miljoe);
        return aap115Rettighet;
    }

    public Map<String, List<NyttVedtakResponse>> genererAap115(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = identService.getUtvalgteIdenterIAldersgruppe(avspillergruppeId, antallNyeIdenter, MIN_ALDER_AAP, MAX_ALDER_AAP - 1, miljoe, null);
        var syntRequest = consumerUtils.createSyntRequest(utvalgteIdenter.size());
        var syntetiserteRettigheter = aapSyntConsumer.syntetiserRettighetAap115(syntRequest);

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            var utvalgtIdent = utvalgteIdenter.remove(utvalgteIdenter.size() - 1);

            var poppStatus = pensjonService.opprettetPersonOgInntektIPopp(utvalgtIdent, miljoe, syntetisertRettighet.getFraDato());
            if (!poppStatus) {
                return Collections.emptyMap();
            }

            arbeidssoekerService.opprettArbeidssoekerAap(utvalgtIdent, miljoe, syntetisertRettighet.getFraDato());

            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetAap115Request(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgtIdent);
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        var identerMedOpprettedeRettigheter = rettighetArenaForvalterConsumer.opprettRettighet(rettigheter);

        serviceUtils.lagreIHodejegeren(identerMedOpprettedeRettigheter);

        return identerMedOpprettedeRettigheter;
    }

    public Map<String, List<NyttVedtakResponse>> genererUngUfoer(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = identService.getUtvalgteIdenterIAldersgruppe(avspillergruppeId, antallNyeIdenter, MIN_ALDER_UNG_UFOER, MAX_ALDER_UNG_UFOER - 1, miljoe, null);
        var syntRequest = consumerUtils.createSyntRequest(utvalgteIdenter.size(), ARENA_AAP_UNG_UFOER_DATE_LIMIT);
        var syntetiserteRettigheter = aapSyntConsumer.syntetiserRettighetUngUfoer(syntRequest);

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            var utvalgtIdent = utvalgteIdenter.remove(utvalgteIdenter.size() - 1);

            arbeidssoekerService.opprettArbeidssoekerAap(utvalgtIdent, miljoe, syntetisertRettighet.getFraDato());

            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetUngUfoerRequest(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgtIdent);
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        var identerMedOpprettedeRettigheter = rettighetArenaForvalterConsumer.opprettRettighet(rettigheter);

        serviceUtils.lagreIHodejegeren(identerMedOpprettedeRettigheter);

        return identerMedOpprettedeRettigheter;
    }

    public Map<String, List<NyttVedtakResponse>> genererTvungenForvaltning(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var identerMedKontonummer = identService.getIdenterMedKontoinformasjon(avspillergruppeId, miljoe, antallNyeIdenter);
        var utvalgteIdenter = identService.getUtvalgteIdenterIAldersgruppe(avspillergruppeId, antallNyeIdenter, MIN_ALDER_AAP, MAX_ALDER_AAP - 1, miljoe, null);
        var syntRequest = consumerUtils.createSyntRequest(utvalgteIdenter.size());
        var syntetiserteRettigheter = aapSyntConsumer.syntetiserRettighetTvungenForvaltning(syntRequest);

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            var utvalgtIdent = utvalgteIdenter.remove(utvalgteIdenter.size() - 1);

            arbeidssoekerService.opprettArbeidssoekerAap(utvalgtIdent, miljoe, syntetisertRettighet.getFraDato());

            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            syntetisertRettighet.setForvalter(ServiceUtils.buildForvalter(identerMedKontonummer.remove(identerMedKontonummer.size() - 1)));
            var rettighetRequest = new RettighetTvungenForvaltningRequest(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgtIdent);
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        var identerMedOpprettedeRettigheter = rettighetArenaForvalterConsumer.opprettRettighet(rettigheter);

        serviceUtils.lagreIHodejegeren(identerMedOpprettedeRettigheter);

        return identerMedOpprettedeRettigheter;
    }

    public Map<String, List<NyttVedtakResponse>> genererFritakMeldekort(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = identService.hentEksisterendeArbeidsoekerIdenter(true);
        var identerIAvspillergruppe = new HashSet<>(identService.getLevende(avspillergruppeId, miljoe));
        utvalgteIdenter.retainAll(identerIAvspillergruppe);
        Collections.shuffle(utvalgteIdenter);
        utvalgteIdenter = utvalgteIdenter.subList(0, antallNyeIdenter);
        var syntRequest = consumerUtils.createSyntRequest(utvalgteIdenter.size());
        var syntetiserteRettigheter = aapSyntConsumer.syntetiserRettighetFritakMeldekort(syntRequest);

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            var utvalgtIdent = utvalgteIdenter.remove(utvalgteIdenter.size() - 1);

            arbeidssoekerService.opprettArbeidssoekerAap(utvalgtIdent, miljoe, syntetisertRettighet.getFraDato());

            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetFritakMeldekortRequest(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgtIdent);
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        var identerMedOpprettedeRettigheter = rettighetArenaForvalterConsumer.opprettRettighet(rettigheter);

        serviceUtils.lagreIHodejegeren(identerMedOpprettedeRettigheter);

        return identerMedOpprettedeRettigheter;
    }
}
