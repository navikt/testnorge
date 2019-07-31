package no.nav.registre.inst.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import no.nav.registre.inst.IdentMedData;
import no.nav.registre.inst.InstSaveInHodejegerenRequest;
import no.nav.registre.inst.Institusjonsopphold;
import no.nav.registre.inst.consumer.rs.HodejegerenConsumer;
import no.nav.registre.inst.consumer.rs.Inst2Consumer;
import no.nav.registre.inst.consumer.rs.InstSyntetisererenConsumer;
import no.nav.registre.inst.provider.rs.requests.SyntetiserInstRequest;
import no.nav.registre.inst.provider.rs.responses.OppholdResponse;

@Service
@Slf4j
public class SyntetiseringService {

    private static final int ANTALL_FORSOEK = 3;
    private static final String INST_NAME = "inst";

    @Autowired
    private IdentService identService;

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private InstSyntetisererenConsumer instSyntetisererenConsumer;

    @Autowired
    private Inst2Consumer inst2Consumer;

    @Autowired
    private Random rand;

    public Map<String, List<OppholdResponse>> finnSyntetiserteMeldinger(SyntetiserInstRequest syntetiserInstRequest, String callId, String consumerId) {
        List<String> utvalgteIdenter = finnLevendeIdenter(syntetiserInstRequest);
        if (utvalgteIdenter.size() < syntetiserInstRequest.getAntallNyeIdenter()) {
            log.warn("Fant ikke nok ledige identer. Lager institusjonsforhold på {} identer.", utvalgteIdenter.size());
        }
        if (utvalgteIdenter.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, Object> tokenObject = inst2Consumer.hentTokenTilInst2();
        List<Institusjonsopphold> syntetiserteMeldinger = hentSyntetiserteInstitusjonsforholdsmeldinger(tokenObject, utvalgteIdenter.size(), callId, consumerId);
        return leggTilInstitusjonsforholdIInst2(tokenObject, utvalgteIdenter, syntetiserteMeldinger, callId, consumerId);
    }

    private List<Institusjonsopphold> hentSyntetiserteInstitusjonsforholdsmeldinger(Map<String, Object> tokenObject, int antallMeldinger,
            String callId, String consumerId) {
        List<Institusjonsopphold> syntetiserteMeldinger = new ArrayList<>(antallMeldinger);
        for (int i = 0; i < ANTALL_FORSOEK && syntetiserteMeldinger.size() < antallMeldinger; i++) {
            syntetiserteMeldinger.addAll(validerOgFjernUgyldigeMeldinger(tokenObject,
                    instSyntetisererenConsumer.hentInstMeldingerFromSyntRest(antallMeldinger - syntetiserteMeldinger.size()),
                    callId,
                    consumerId));
        }
        return syntetiserteMeldinger;
    }

    private List<String> finnLevendeIdenter(SyntetiserInstRequest syntetiserInstRequest) {
        List<String> alleLevendeIdenter = hodejegerenConsumer.finnLevendeIdenter(syntetiserInstRequest.getAvspillergruppeId());
        List<String> utvalgteIdenter = new ArrayList<>(syntetiserInstRequest.getAntallNyeIdenter());

        for (int i = 0; i < syntetiserInstRequest.getAntallNyeIdenter(); i++) {
            if (!alleLevendeIdenter.isEmpty()) {
                utvalgteIdenter.add(alleLevendeIdenter.remove(rand.nextInt(alleLevendeIdenter.size())));
            }
        }

        return utvalgteIdenter;
    }

    private Map<String, List<OppholdResponse>> leggTilInstitusjonsforholdIInst2(Map<String, Object> tokenObject, List<String> identer,
            List<Institusjonsopphold> syntetiserteMeldinger, String callId, String consumerId) {
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
                    identService.hentInstitusjonsoppholdFraInst2(tokenObject, personident, callId, consumerId);
            if (!eksisterendeInstitusjonsforhold.isEmpty()) {
                log.warn("Ident {} har allerede fått opprettet institusjonsforhold. Hopper over opprettelse.", personident);
            } else {
                institusjonsopphold.setPersonident(personident);
                OppholdResponse oppholdResponse = inst2Consumer.leggTilInstitusjonsoppholdIInst2(tokenObject, institusjonsopphold, callId, consumerId);
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

        List<String> lagredeIdenter = hodejegerenConsumer.saveHistory(hodejegerenRequest);

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

    private List<Institusjonsopphold> validerOgFjernUgyldigeMeldinger(Map<String, Object> tokenObject, List<Institusjonsopphold> syntetiserteMeldinger,
            String callId, String consumerId) {
        List<Institusjonsopphold> gyldigeSyntetiserteMeldinger = new ArrayList<>(syntetiserteMeldinger.size());

        for (Institusjonsopphold melding : syntetiserteMeldinger) {
            String tssEksternId = melding.getTssEksternId();
            String startdato = melding.getStartdato();
            String faktiskSluttdato = melding.getFaktiskSluttdato();
            if (inst2Consumer.finnesInstitusjonPaaDato(tokenObject, tssEksternId, startdato, callId, consumerId).is2xxSuccessful()
                    && inst2Consumer.finnesInstitusjonPaaDato(tokenObject, tssEksternId, faktiskSluttdato, callId, consumerId).is2xxSuccessful()) {
                gyldigeSyntetiserteMeldinger.add(melding);
            }
        }

        return gyldigeSyntetiserteMeldinger;
    }
}
