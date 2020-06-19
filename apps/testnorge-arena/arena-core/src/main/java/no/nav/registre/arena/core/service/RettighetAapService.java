package no.nav.registre.arena.core.service;

import static no.nav.registre.arena.core.consumer.rs.util.ConsumerUtils.getFoedselsdatoFraFnr;
import static no.nav.registre.arena.core.service.util.ServiceUtils.BEGRUNNELSE;
import static no.nav.registre.arena.core.service.util.ServiceUtils.MAX_ALDER_AAP;
import static no.nav.registre.arena.core.service.util.ServiceUtils.MAX_ALDER_UNG_UFOER;
import static no.nav.registre.arena.core.service.util.ServiceUtils.MIN_ALDER_AAP;
import static no.nav.registre.arena.core.service.util.ServiceUtils.MIN_ALDER_UNG_UFOER;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import org.springframework.stereotype.Service;

import java.util.*;

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

@Slf4j
@Service
@RequiredArgsConstructor
public class RettighetAapService {

    private final AapSyntConsumer aapSyntConsumer;
    private final RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;
    private final BrukereService brukereService;
    private final ServiceUtils serviceUtils;
    private final PensjonTestdataFacadeConsumer pensjonTestdataFacadeConsumer;
    private final Random rand;

    public final static String SYKEPENGEERSTATNING = "SPE";
    public final static int SYKEPENGEERSTATNING_MAKS_PERIODE = 6;

    public Map<String, List<NyttVedtakResponse>> genererAapMedTilhoerende115(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = serviceUtils.getUtvalgteIdenterIAldersgruppe(avspillergruppeId, antallNyeIdenter, MIN_ALDER_AAP, MAX_ALDER_AAP - 1, miljoe);
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
                if(SYKEPENGEERSTATNING.equals(rettighet.getAktivitetsfase())){
                    serviceUtils.setDatoPeriodeVedtakInnenforMaxAntallMaaneder(rettighet, SYKEPENGEERSTATNING_MAKS_PERIODE);
                }
            });

            rettigheter.add(rettighetRequest);
        }

        rettighetArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoekerAap(aap115Rettigheter, miljoe));
        var identerMedOpprettedeRettigheter = rettighetArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoekerAap(rettigheter, miljoe));

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

        if(SYKEPENGEERSTATNING.equals(rettighetRequest.getNyeAap().get(0).getAktivitetsfase())){
            serviceUtils.setDatoPeriodeVedtakInnenforMaxAntallMaaneder(rettighetRequest.getNyeAap().get(0), SYKEPENGEERSTATNING_MAKS_PERIODE);
        }

        rettighetArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoekerAap(new ArrayList<>(Collections.singletonList(aap115Rettighet)), miljoe));
        return rettighetArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoekerAap(new ArrayList<>(Collections.singletonList(rettighetRequest)), miljoe));
    }

    public Map<String, List<NyttVedtakResponse>> genererAap115(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = serviceUtils.getUtvalgteIdenterIAldersgruppe(avspillergruppeId, antallNyeIdenter, MIN_ALDER_AAP, MAX_ALDER_AAP - 1, miljoe);
        var syntetiserteRettigheter = aapSyntConsumer.syntetiserRettighetAap115(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetAap115Request(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        var identerMedOpprettedeRettigheter = rettighetArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoekerAap(rettigheter, miljoe));

        serviceUtils.lagreIHodejegeren(identerMedOpprettedeRettigheter);

        return identerMedOpprettedeRettigheter;
    }

    public Map<String, List<NyttVedtakResponse>> genererUngUfoer(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = serviceUtils.getUtvalgteIdenterIAldersgruppe(avspillergruppeId, antallNyeIdenter, MIN_ALDER_UNG_UFOER, MAX_ALDER_UNG_UFOER - 1, miljoe);
        var syntetiserteRettigheter = aapSyntConsumer.syntetiserRettighetUngUfoer(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetUngUfoerRequest(Collections.singletonList(syntetisertRettighet));
            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        var identerMedOpprettedeRettigheter = rettighetArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoekerAap(rettigheter, miljoe));

        serviceUtils.lagreIHodejegeren(identerMedOpprettedeRettigheter);

        return identerMedOpprettedeRettigheter;
    }

    public Map<String, List<NyttVedtakResponse>> genererTvungenForvaltning(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var identerMedKontonummer = serviceUtils.getIdenterMedKontoinformasjon(avspillergruppeId, miljoe, antallNyeIdenter);
        var utvalgteIdenter = serviceUtils.getUtvalgteIdenterIAldersgruppe(avspillergruppeId, antallNyeIdenter, MIN_ALDER_AAP, MAX_ALDER_AAP - 1, miljoe);
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

        var identerMedOpprettedeRettigheter = rettighetArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoekerAap(rettigheter, miljoe));

        serviceUtils.lagreIHodejegeren(identerMedOpprettedeRettigheter);

        return identerMedOpprettedeRettigheter;
    }

    public Map<String, List<NyttVedtakResponse>> genererFritakMeldekort(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = brukereService.hentEksisterendeArbeidsoekerIdenter();
        var identerIAvspillergruppe = new HashSet<>(serviceUtils.getLevende(avspillergruppeId, miljoe));
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
                .fodselsDato(getFoedselsdatoFraFnr(ident))
                .miljoer(Collections.singletonList(miljoe))
                .fnr(ident)
                .build());

        for (var response : opprettPersonStatus.getStatus()) {
            if (response.getResponse().getHttpStatus().getStatus() != 200) {
                log.error(
                        "Kunne ikke opprette ident {} i popp i miljø {}. Feilmelding: {}",
                        ident.replaceAll("[\r\n]", ""),
                        response.getMiljo().replaceAll("[\r\n]", ""),
                        response.getResponse().getMessage().replaceAll("[\r\n]", "")
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
                        ident.replaceAll("[\r\n]", ""),
                        response.getMiljo().replaceAll("[\r\n]", ""),
                        response.getResponse().getMessage().replaceAll("[\r\n]", "")
                );
            }
        }
    }
}
