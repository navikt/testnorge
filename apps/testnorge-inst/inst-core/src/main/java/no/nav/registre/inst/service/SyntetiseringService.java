package no.nav.registre.inst.service;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inst.IdentMedData;
import no.nav.registre.inst.InstSaveInHodejegerenRequest;
import no.nav.registre.inst.InstitusjonsoppholdV2;
import no.nav.registre.inst.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.inst.consumer.rs.Inst2Consumer;
import no.nav.registre.inst.consumer.rs.InstSyntetisererenConsumer;
import no.nav.registre.inst.provider.rs.requests.SyntetiserInstRequest;
import no.nav.registre.inst.provider.rs.responses.OppholdResponse;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyntetiseringService {

    private static final int ANTALL_FORSOEK = 3;
    private static final String INST_NAME = "inst";

    private final IdentService identService;
    private final HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;
    private final HodejegerenConsumer hodejegerenConsumer;
    private final InstSyntetisererenConsumer instSyntetisererenConsumer;
    private final Inst2Consumer inst2Consumer;
    private final Random rand;

    public Map<String, List<OppholdResponse>> finnSyntetiserteMeldingerOgLagreIInst2(
            String callId,
            String consumerId,
            String miljoe,
            SyntetiserInstRequest syntetiserInstRequest
    ) {
        var utvalgteIdenter = finnLevendeIdenter(syntetiserInstRequest);
        if (utvalgteIdenter.size() < syntetiserInstRequest.getAntallNyeIdenter()) {
            log.warn("Fant ikke nok ledige identer. Lager institusjonsforhold på {} identer.", utvalgteIdenter.size());
        }
        if (utvalgteIdenter.isEmpty()) {
            return new HashMap<>();
        }

        var bearerToken = identService.hentTokenTilInst2(miljoe);
        var syntetiserteMeldinger = hentSyntetiserteInstitusjonsforholdsmeldinger(bearerToken, callId, consumerId, miljoe, utvalgteIdenter.size());
        return leggTilInstitusjonsforholdIInst2(bearerToken, callId, consumerId, miljoe, utvalgteIdenter, syntetiserteMeldinger);
    }

    private List<InstitusjonsoppholdV2> hentSyntetiserteInstitusjonsforholdsmeldinger(
            String bearerToken,
            String callId,
            String consumerId,
            String miljoe,
            int antallMeldinger
    ) {
        List<InstitusjonsoppholdV2> syntetiserteMeldinger = new ArrayList<>(antallMeldinger);
        for (int i = 0; i < ANTALL_FORSOEK && syntetiserteMeldinger.size() < antallMeldinger; i++) {
            syntetiserteMeldinger.addAll(validerOgFjernUgyldigeMeldinger(bearerToken, callId, consumerId, miljoe,
                    instSyntetisererenConsumer.hentInstMeldingerFromSyntRest(antallMeldinger - syntetiserteMeldinger.size())));
        }
        return syntetiserteMeldinger;
    }

    private List<String> finnLevendeIdenter(
            SyntetiserInstRequest syntetiserInstRequest
    ) {
        var alleLevendeIdenter = getLevendeIdenter(syntetiserInstRequest.getAvspillergruppeId());
        List<String> utvalgteIdenter = new ArrayList<>(syntetiserInstRequest.getAntallNyeIdenter());

        for (int i = 0; i < syntetiserInstRequest.getAntallNyeIdenter(); i++) {
            if (!alleLevendeIdenter.isEmpty()) {
                utvalgteIdenter.add(alleLevendeIdenter.remove(rand.nextInt(alleLevendeIdenter.size())));
            }
        }

        return utvalgteIdenter;
    }

    private Map<String, List<OppholdResponse>> leggTilInstitusjonsforholdIInst2(
            String bearerToken,
            String callId,
            String consumerId,
            String miljoe,
            List<String> identer,
            List<InstitusjonsoppholdV2> syntetiserteMeldinger
    ) {
        var utvalgteIdenter = new ArrayList<>(identer);
        List<InstitusjonsoppholdV2> historikkSomSkalLagres = new ArrayList<>();
        Map<String, List<OppholdResponse>> statusFraInst2 = new HashMap<>();
        var antallOppholdOpprettet = 0;

        for (var institusjonsopphold : syntetiserteMeldinger) {
            if (utvalgteIdenter.isEmpty()) {
                break;
            }
            var personident = utvalgteIdenter.remove(0);
            var eksisterendeInstitusjonsforhold =
                    identService.hentInstitusjonsoppholdFraInst2(bearerToken, callId, consumerId, miljoe, personident);
            if (!eksisterendeInstitusjonsforhold.isEmpty()) {
                log.warn("Ident {} har allerede fått opprettet institusjonsforhold. Hopper over opprettelse.", personident);
            } else {
                institusjonsopphold.setNorskident(personident);
                OppholdResponse oppholdResponse = fyllOppholdResponse(bearerToken, callId, consumerId, miljoe, statusFraInst2, institusjonsopphold, personident);

                if (oppholdResponse.getStatus().is2xxSuccessful()) {
                    antallOppholdOpprettet++;
                    historikkSomSkalLagres.add(institusjonsopphold);
                }
            }
        }

        List<IdentMedData> identerMedData = new ArrayList<>(historikkSomSkalLagres.size());
        for (var institusjonsopphold : historikkSomSkalLagres) {
            identerMedData.add(new IdentMedData(institusjonsopphold.getNorskident(), Collections.singletonList(institusjonsopphold)));
        }
        var hodejegerenRequest = new InstSaveInHodejegerenRequest(INST_NAME, identerMedData);

        if (antallOppholdOpprettet < identer.size()) {
            log.warn("Kunne ikke opprette institusjonsopphold på alle identer. Antall opphold opprettet: {}", antallOppholdOpprettet);
        }

        var lagredeIdenter = hodejegerenHistorikkConsumer.saveHistory(hodejegerenRequest);

        if (lagredeIdenter.size() < identerMedData.size()) {
            List<String> identerSomIkkeBleLagret = new ArrayList<>(identerMedData.size());
            for (var ident : identerMedData) {
                identerSomIkkeBleLagret.add(ident.getId());
            }
            identerSomIkkeBleLagret.removeAll(lagredeIdenter);
            log.warn("Kunne ikke lagre historikk på alle identer. Identer som ikke ble lagret: {}", identerSomIkkeBleLagret);
        }

        return statusFraInst2;
    }

    private OppholdResponse fyllOppholdResponse(String bearerToken, String callId, String consumerId, String miljoe, Map<String, List<OppholdResponse>> statusFraInst2, InstitusjonsoppholdV2 institusjonsopphold, String personident) {
        var oppholdResponse = inst2Consumer.leggTilInstitusjonsoppholdIInst2(bearerToken, callId, consumerId, miljoe, institusjonsopphold);
        if (statusFraInst2.containsKey(personident)) {
            statusFraInst2.get(personident).add(oppholdResponse);
        } else {
            List<OppholdResponse> oppholdResponses = new ArrayList<>();
            oppholdResponses.add(oppholdResponse);
            statusFraInst2.put(personident, oppholdResponses);
        }
        return oppholdResponse;
    }

    private List<InstitusjonsoppholdV2> validerOgFjernUgyldigeMeldinger(
            String bearerToken,
            String callId,
            String consumerId,
            String miljoe,
            List<InstitusjonsoppholdV2> syntetiserteMeldinger
    ) {
        List<InstitusjonsoppholdV2> gyldigeSyntetiserteMeldinger = new ArrayList<>(syntetiserteMeldinger.size());

        for (var melding : syntetiserteMeldinger) {
            var tssEksternId = melding.getTssEksternId();
            var startdato = melding.getStartdato();
            var faktiskSluttdato = melding.getSluttdato();
            if (inst2Consumer.finnesInstitusjonPaaDato(bearerToken, callId, consumerId, miljoe, tssEksternId, startdato).is2xxSuccessful()
                    && inst2Consumer.finnesInstitusjonPaaDato(bearerToken, callId, consumerId, miljoe, tssEksternId, faktiskSluttdato).is2xxSuccessful()) {
                gyldigeSyntetiserteMeldinger.add(melding);
            }
        }

        return gyldigeSyntetiserteMeldinger;
    }

    @Timed(value = "inst.resource.latency", extraTags = { "operation", "hodejegeren" })
    private List<String> getLevendeIdenter(
            Long avspillergruppeId
    ) {
        return hodejegerenConsumer.getLevende(avspillergruppeId);
    }
}
