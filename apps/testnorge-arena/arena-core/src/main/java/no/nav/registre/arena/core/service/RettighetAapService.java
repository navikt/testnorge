package no.nav.registre.arena.core.service;

import static no.nav.registre.arena.core.service.util.ServiceUtils.AKTIVITETSFASE_SYKEPENGEERSTATNING;
import static no.nav.registre.arena.core.service.util.ServiceUtils.BEGRUNNELSE;
import static no.nav.registre.arena.core.service.util.ServiceUtils.MAX_ALDER_AAP;
import static no.nav.registre.arena.core.service.util.ServiceUtils.MAX_ALDER_UNG_UFOER;
import static no.nav.registre.arena.core.service.util.ServiceUtils.MIN_ALDER_AAP;
import static no.nav.registre.arena.core.service.util.ServiceUtils.MIN_ALDER_UNG_UFOER;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.service.util.ArbeidssoekerUtils;
import no.nav.registre.arena.core.service.util.IdenterUtils;
import no.nav.registre.arena.core.service.util.VedtakUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import no.nav.registre.arena.core.consumer.rs.AapSyntConsumer;
import no.nav.registre.arena.core.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.request.RettighetAap115Request;
import no.nav.registre.arena.core.consumer.rs.request.RettighetAapRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetFritakMeldekortRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTvungenForvaltningRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetUngUfoerRequest;
import no.nav.registre.arena.core.pensjon.consumer.rs.PensjonTestdataFacadeConsumer;
import no.nav.registre.arena.core.pensjon.request.PensjonTestdataInntekt;
import no.nav.registre.arena.core.pensjon.request.PensjonTestdataPerson;
import no.nav.registre.arena.core.service.util.ServiceUtils;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.libs.core.util.IdentUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class RettighetAapService {

    private static final String REGEX_RN = "[\r\n]";

    private final AapSyntConsumer aapSyntConsumer;
    private final RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;
    private final BrukereService brukereService;
    private final ServiceUtils serviceUtils;
    private final IdenterUtils identerUtils;
    private final ArbeidssoekerUtils arbeidsoekerUtils;
    private final VedtakUtils vedtakUtils;
    private final PensjonTestdataFacadeConsumer pensjonTestdataFacadeConsumer;
    private final Random rand;

    public static final int SYKEPENGEERSTATNING_MAKS_PERIODE = 6;

    public Map<String, List<NyttVedtakResponse>> genererAapMedTilhoerende115(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = identerUtils.getUtvalgteIdenterIAldersgruppe(avspillergruppeId, antallNyeIdenter, MIN_ALDER_AAP, MAX_ALDER_AAP - 1, miljoe);
        var syntetiserteRettigheter = aapSyntConsumer.syntetiserRettighetAap(utvalgteIdenter.size());

        List<RettighetRequest> aap115Rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            var ident = utvalgteIdenter.remove(utvalgteIdenter.size() - 1);

            opprettPersonOgInntektIPopp(ident, miljoe, syntetisertRettighet);

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

            rettighetRequest.getNyeAap().forEach(rettighet -> {
                if(AKTIVITETSFASE_SYKEPENGEERSTATNING.equals(rettighet.getAktivitetsfase())){
                    vedtakUtils.setDatoPeriodeVedtakInnenforMaxAntallMaaneder(rettighet, SYKEPENGEERSTATNING_MAKS_PERIODE);
                }
            });

            rettigheter.add(rettighetRequest);
        }

        rettighetArenaForvalterConsumer.opprettRettighet(arbeidsoekerUtils.opprettArbeidssoekerAap(aap115Rettigheter, miljoe));
        var identerMedOpprettedeRettigheter = rettighetArenaForvalterConsumer.opprettRettighet(arbeidsoekerUtils.opprettArbeidssoekerAap(rettigheter, miljoe));

        serviceUtils.lagreIHodejegeren(identerMedOpprettedeRettigheter);

        return identerMedOpprettedeRettigheter;
    }

    public Map<String, List<NyttVedtakResponse>> genererAapMedTilhoerende115(
            String ident,
            String miljoe
    ) {
        var syntetisertRettighet = aapSyntConsumer.syntetiserRettighetAap(1).get(0);

        opprettPersonOgInntektIPopp(ident, miljoe, syntetisertRettighet);

        var aap115 = aapSyntConsumer.syntetiserRettighetAap115(syntetisertRettighet.getFraDato().minusDays(1), syntetisertRettighet.getTilDato()).get(0);
        aap115.setBegrunnelse(BEGRUNNELSE);
        var aap115Rettighet = new RettighetAap115Request(Collections.singletonList(aap115));
        aap115Rettighet.setPersonident(ident);
        aap115Rettighet.setMiljoe(miljoe);

        syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
        var rettighetRequest = new RettighetAapRequest(Collections.singletonList(syntetisertRettighet));
        rettighetRequest.setPersonident(ident);
        rettighetRequest.setMiljoe(miljoe);

        if(AKTIVITETSFASE_SYKEPENGEERSTATNING.equals(rettighetRequest.getNyeAap().get(0).getAktivitetsfase())){
            vedtakUtils.setDatoPeriodeVedtakInnenforMaxAntallMaaneder(rettighetRequest.getNyeAap().get(0), SYKEPENGEERSTATNING_MAKS_PERIODE);
        }

        rettighetArenaForvalterConsumer.opprettRettighet(arbeidsoekerUtils.opprettArbeidssoekerAap(new ArrayList<>(Collections.singletonList(aap115Rettighet)), miljoe));
        return rettighetArenaForvalterConsumer.opprettRettighet(arbeidsoekerUtils.opprettArbeidssoekerAap(new ArrayList<>(Collections.singletonList(rettighetRequest)), miljoe));
    }

    public Map<String, List<NyttVedtakResponse>> genererAap115(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = identerUtils.getUtvalgteIdenterIAldersgruppe(avspillergruppeId, antallNyeIdenter, MIN_ALDER_AAP, MAX_ALDER_AAP - 1, miljoe);
        var syntetiserteRettigheter = aapSyntConsumer.syntetiserRettighetAap115(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetAap115Request(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        var identerMedOpprettedeRettigheter = rettighetArenaForvalterConsumer.opprettRettighet(arbeidsoekerUtils.opprettArbeidssoekerAap(rettigheter, miljoe));

        serviceUtils.lagreIHodejegeren(identerMedOpprettedeRettigheter);

        return identerMedOpprettedeRettigheter;
    }

    public Map<String, List<NyttVedtakResponse>> genererUngUfoer(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = identerUtils.getUtvalgteIdenterIAldersgruppe(avspillergruppeId, antallNyeIdenter, MIN_ALDER_UNG_UFOER, MAX_ALDER_UNG_UFOER - 1, miljoe);
        var syntetiserteRettigheter = aapSyntConsumer.syntetiserRettighetUngUfoer(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetUngUfoerRequest(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        var identerMedOpprettedeRettigheter = rettighetArenaForvalterConsumer.opprettRettighet(arbeidsoekerUtils.opprettArbeidssoekerAap(rettigheter, miljoe));

        serviceUtils.lagreIHodejegeren(identerMedOpprettedeRettigheter);

        return identerMedOpprettedeRettigheter;
    }

    public Map<String, List<NyttVedtakResponse>> genererTvungenForvaltning(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var identerMedKontonummer = identerUtils.getIdenterMedKontoinformasjon(avspillergruppeId, miljoe, antallNyeIdenter);
        var utvalgteIdenter = identerUtils.getUtvalgteIdenterIAldersgruppe(avspillergruppeId, antallNyeIdenter, MIN_ALDER_AAP, MAX_ALDER_AAP - 1, miljoe);
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

        var identerMedOpprettedeRettigheter = rettighetArenaForvalterConsumer.opprettRettighet(arbeidsoekerUtils.opprettArbeidssoekerAap(rettigheter, miljoe));

        serviceUtils.lagreIHodejegeren(identerMedOpprettedeRettigheter);

        return identerMedOpprettedeRettigheter;
    }

    public Map<String, List<NyttVedtakResponse>> genererFritakMeldekort(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = brukereService.hentEksisterendeArbeidsoekerIdenter();
        var identerIAvspillergruppe = new HashSet<>(identerUtils.getLevende(avspillergruppeId, miljoe));
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

        var identerMedOpprettedeRettigheter = rettighetArenaForvalterConsumer.opprettRettighet(rettigheter);

        serviceUtils.lagreIHodejegeren(identerMedOpprettedeRettigheter);

        return identerMedOpprettedeRettigheter;
    }

    void opprettPersonOgInntektIPopp(
            String ident,
            String miljoe,
            NyttVedtakAap syntetisertRettighet
    ) {
        var opprettPersonStatus = pensjonTestdataFacadeConsumer.opprettPerson(PensjonTestdataPerson.builder()
                .bostedsland("NOR")
                .fodselsDato(IdentUtil.getFoedselsdatoFraIdent(ident))
                .miljoer(Collections.singletonList(miljoe))
                .fnr(ident)
                .build());

        for (var response : opprettPersonStatus.getStatus()) {
            if (response.getResponse().getHttpStatus().getStatus() != 200) {
                log.error(
                        "Kunne ikke opprette ident {} i popp i miljø {}. Feilmelding: {}",
                        ident.replaceAll(REGEX_RN, ""),
                        response.getMiljo().replaceAll(REGEX_RN, ""),
                        response.getResponse().getMessage().replaceAll(REGEX_RN, "")
                );
            }
        }

        var opprettInntektStatus = pensjonTestdataFacadeConsumer.opprettInntekt(PensjonTestdataInntekt.builder()
                .belop(rand.nextInt(650_000) + 450_000)
                .fnr(ident)
                .fomAar(syntetisertRettighet.getFraDato().minusYears(4).getYear())
                .miljoer(Collections.singletonList(miljoe))
                .redusertMedGrunnbelop(true)
                .tomAar(syntetisertRettighet.getFraDato().minusYears(1).getYear())
                .build());

        for (var response : opprettInntektStatus.getStatus()) {
            if (response.getResponse().getHttpStatus().getStatus() != 200) {
                log.error(
                        "Kunne ikke opprette inntekt på ident {} i popp i miljø {}. Feilmelding: {}",
                        ident.replaceAll(REGEX_RN, ""),
                        response.getMiljo().replaceAll(REGEX_RN, ""),
                        response.getResponse().getMessage().replaceAll(REGEX_RN, "")
                );
            }
        }
    }
}
