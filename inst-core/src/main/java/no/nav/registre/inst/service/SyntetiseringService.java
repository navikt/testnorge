package no.nav.registre.inst.service;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import no.nav.registre.inst.IdentMedData;
import no.nav.registre.inst.InstSaveInHodejegerenRequest;
import no.nav.registre.inst.Institusjonsopphold;
import no.nav.registre.inst.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.inst.consumer.rs.Inst2Consumer;
import no.nav.registre.inst.consumer.rs.InstSyntetisererenConsumer;
import no.nav.registre.inst.provider.rs.requests.SyntetiserInstRequest;
import no.nav.registre.inst.provider.rs.responses.OppholdResponse;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@Service
@Slf4j
public class SyntetiseringService {

    private static final int ANTALL_FORSOEK = 3;
    private static final String INST_NAME = "inst";

    @Autowired
    private IdentService identService;

    @Autowired
    private HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private InstSyntetisererenConsumer instSyntetisererenConsumer;

    @Autowired
    private Inst2Consumer inst2Consumer;

    @Autowired
    private Inst2FasitService inst2FasitService;

    @Autowired
    private Random rand;

    public Map<String, List<OppholdResponse>> finnSyntetiserteMeldingerOgLagreIInst2(String callId, String consumerId, String miljoe, SyntetiserInstRequest syntetiserInstRequest) {
        List<String> utvalgteIdenter = finnLevendeIdenter(syntetiserInstRequest);
        if (utvalgteIdenter.size() < syntetiserInstRequest.getAntallNyeIdenter()) {
            log.warn("Fant ikke nok ledige identer. Lager institusjonsforhold på {} identer.", utvalgteIdenter.size());
        }
        if (utvalgteIdenter.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, Object> tokenObject = inst2Consumer.hentTokenTilInst2(inst2FasitService.getFregTokenProviderInEnvironment(miljoe));
        List<Institusjonsopphold> syntetiserteMeldinger = hentSyntetiserteInstitusjonsforholdsmeldinger(tokenObject, callId, consumerId, miljoe, utvalgteIdenter.size());
        return leggTilInstitusjonsforholdIInst2(tokenObject, callId, consumerId, miljoe, utvalgteIdenter, syntetiserteMeldinger);
    }

    private List<Institusjonsopphold> hentSyntetiserteInstitusjonsforholdsmeldinger(Map<String, Object> tokenObject, String callId, String consumerId,
            String miljoe, int antallMeldinger) {
        List<Institusjonsopphold> syntetiserteMeldinger = new ArrayList<>(antallMeldinger);
        for (int i = 0; i < ANTALL_FORSOEK && syntetiserteMeldinger.size() < antallMeldinger; i++) {
            syntetiserteMeldinger.addAll(validerOgFjernUgyldigeMeldinger(tokenObject, callId, consumerId, miljoe,
                    instSyntetisererenConsumer.hentInstMeldingerFromSyntRest(antallMeldinger - syntetiserteMeldinger.size())));
        }
        return syntetiserteMeldinger;
    }

    private List<String> finnLevendeIdenter(SyntetiserInstRequest syntetiserInstRequest) {
        List<String> alleLevendeIdenter = getLevendeIdenter(syntetiserInstRequest.getAvspillergruppeId());
        List<String> utvalgteIdenter = new ArrayList<>(syntetiserInstRequest.getAntallNyeIdenter());

        for (int i = 0; i < syntetiserInstRequest.getAntallNyeIdenter(); i++) {
            if (!alleLevendeIdenter.isEmpty()) {
                utvalgteIdenter.add(alleLevendeIdenter.remove(rand.nextInt(alleLevendeIdenter.size())));
            }
        }

        return utvalgteIdenter;
    }

    private Map<String, List<OppholdResponse>> leggTilInstitusjonsforholdIInst2(Map<String, Object> tokenObject, String callId, String consumerId, String miljoe, List<String> identer,
            List<Institusjonsopphold> syntetiserteMeldinger) {
        List<String> utvalgteIdenter = new ArrayList<>(identer);
        List<Institusjonsopphold> historikkSomSkalLagres = new ArrayList<>();
        Map<String, List<OppholdResponse>> statusFraInst2 = new HashMap<>();
        int antallOppholdOpprettet = 0;

        for (Institusjonsopphold institusjonsopphold : syntetiserteMeldinger) {
            if (utvalgteIdenter.isEmpty()) {
                break;
            }
            String personident = utvalgteIdenter.remove(0);
            List<Institusjonsopphold> eksisterendeInstitusjonsforhold =
                    identService.hentInstitusjonsoppholdFraInst2(tokenObject, callId, consumerId, miljoe, personident);
            if (!eksisterendeInstitusjonsforhold.isEmpty()) {
                log.warn("Ident {} har allerede fått opprettet institusjonsforhold. Hopper over opprettelse.", personident);
            } else {
                institusjonsopphold.setPersonident(personident);
                OppholdResponse oppholdResponse = inst2Consumer.leggTilInstitusjonsoppholdIInst2(tokenObject, callId, consumerId, miljoe, institusjonsopphold);
                if (statusFraInst2.containsKey(personident)) {
                    statusFraInst2.get(personident).add(oppholdResponse);
                } else {
                    List<OppholdResponse> oppholdResponses = new ArrayList<>();
                    oppholdResponses.add(oppholdResponse);
                    statusFraInst2.put(personident, oppholdResponses);
                }

                if (oppholdResponse.getStatus().is2xxSuccessful()) {
                    antallOppholdOpprettet++;
                    historikkSomSkalLagres.add(institusjonsopphold);
                }
            }
        }

        List<IdentMedData> identerMedData = new ArrayList<>(historikkSomSkalLagres.size());
        for (Institusjonsopphold institusjonsopphold : historikkSomSkalLagres) {
            identerMedData.add(new IdentMedData(institusjonsopphold.getPersonident(), Collections.singletonList(institusjonsopphold)));
        }
        InstSaveInHodejegerenRequest hodejegerenRequest = new InstSaveInHodejegerenRequest(INST_NAME, identerMedData);

        if (antallOppholdOpprettet < identer.size()) {
            log.warn("Kunne ikke opprette institusjonsopphold på alle identer. Antall opphold opprettet: {}", antallOppholdOpprettet);
        }

        List<String> lagredeIdenter = hodejegerenHistorikkConsumer.saveHistory(hodejegerenRequest);

        if (lagredeIdenter.size() < identerMedData.size()) {
            List<String> identerSomIkkeBleLagret = new ArrayList<>(identerMedData.size());
            for (IdentMedData ident : identerMedData) {
                identerSomIkkeBleLagret.add(ident.getId());
            }
            identerSomIkkeBleLagret.removeAll(lagredeIdenter);
            log.warn("Kunne ikke lagre historikk på alle identer. Identer som ikke ble lagret: {}", identerSomIkkeBleLagret);
        }

        return statusFraInst2;
    }

    private List<Institusjonsopphold> validerOgFjernUgyldigeMeldinger(Map<String, Object> tokenObject, String callId, String consumerId, String miljoe, List<Institusjonsopphold> syntetiserteMeldinger) {
        List<Institusjonsopphold> gyldigeSyntetiserteMeldinger = new ArrayList<>(syntetiserteMeldinger.size());

        for (Institusjonsopphold melding : syntetiserteMeldinger) {
            String tssEksternId = melding.getTssEksternId();
            LocalDate startdato = melding.getStartdato();
            LocalDate faktiskSluttdato = melding.getFaktiskSluttdato();
            if (inst2Consumer.finnesInstitusjonPaaDato(tokenObject, callId, consumerId, miljoe, tssEksternId, startdato).is2xxSuccessful()
                    && inst2Consumer.finnesInstitusjonPaaDato(tokenObject, callId, consumerId, miljoe, tssEksternId, faktiskSluttdato).is2xxSuccessful()) {
                gyldigeSyntetiserteMeldinger.add(melding);
            }
        }

        return gyldigeSyntetiserteMeldinger;
    }

    @Timed(value = "inst.resource.latency", extraTags = { "operation", "hodejegeren" })
    private List<String> getLevendeIdenter(Long avspillergruppeId) {
        return hodejegerenConsumer.getLevende(avspillergruppeId);
    }
}
