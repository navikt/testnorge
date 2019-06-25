package no.nav.registre.inst.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import no.nav.registre.inst.IdentMedData;
import no.nav.registre.inst.InstSaveInHodejegerenRequest;
import no.nav.registre.inst.Institusjonsforholdsmelding;
import no.nav.registre.inst.consumer.rs.HodejegerenConsumer;
import no.nav.registre.inst.consumer.rs.Inst2Consumer;
import no.nav.registre.inst.consumer.rs.InstSyntetisererenConsumer;
import no.nav.registre.inst.provider.rs.requests.SyntetiserInstRequest;

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

    public List<ResponseEntity> finnSyntetiserteMeldinger(SyntetiserInstRequest syntetiserInstRequest) {
        List<String> utvalgteIdenter = finnLevendeIdenter(syntetiserInstRequest);
        if (utvalgteIdenter.size() < syntetiserInstRequest.getAntallNyeIdenter()) {
            log.warn("Fant ikke nok ledige identer. Lager institusjonsforhold på {} identer.", utvalgteIdenter.size());
        }
        if (utvalgteIdenter.isEmpty()) {
            List<ResponseEntity> tomRespons = new ArrayList<>();
            tomRespons.add(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fant ingen ledige identer i avspillergruppe " + syntetiserInstRequest.getAvspillergruppeId()));
            return tomRespons;
        }

        Map<String, Object> tokenObject = inst2Consumer.hentTokenTilInst2();
        List<Institusjonsforholdsmelding> syntetiserteMeldinger = hentSyntetiserteInstitusjonsforholdsmeldinger(tokenObject, utvalgteIdenter.size());
        return leggTilInstitusjonsforholdIInst2(tokenObject, utvalgteIdenter, syntetiserteMeldinger);
    }

    public List<ResponseEntity> opprettInstitusjonsforholdIIInst2(List<Institusjonsforholdsmelding> meldinger) {
        return leggTilInstitusjonsforholdIInst2(inst2Consumer.hentTokenTilInst2(),
                meldinger.parallelStream()
                        .map(Institusjonsforholdsmelding::getPersonident)
                        .collect(Collectors.toList()),
                meldinger);
    }

    private List<Institusjonsforholdsmelding> hentSyntetiserteInstitusjonsforholdsmeldinger(Map<String, Object> tokenObject, int antallMeldinger) {
        List<Institusjonsforholdsmelding> syntetiserteMeldinger = new ArrayList<>(antallMeldinger);
        for (int i = 0; i < ANTALL_FORSOEK && syntetiserteMeldinger.size() < antallMeldinger; i++) {
            syntetiserteMeldinger.addAll(validerOgFjernUgyldigeMeldinger(tokenObject, instSyntetisererenConsumer.hentInstMeldingerFromSyntRest(antallMeldinger - syntetiserteMeldinger.size())));
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

    private List<ResponseEntity> leggTilInstitusjonsforholdIInst2(Map<String, Object> tokenObject, List<String> identer, List<Institusjonsforholdsmelding> syntetiserteMeldinger) {
        List<ResponseEntity> responseEntities = new ArrayList<>();
        List<String> utvalgteIdenter = new ArrayList<>(identer);
        List<Institusjonsforholdsmelding> historikkSomSkalLagres = new ArrayList<>();
        int antallOppholdOpprettet = 0;

        for (Institusjonsforholdsmelding institusjonsforholdsmelding : syntetiserteMeldinger) {
            if (utvalgteIdenter.isEmpty()) {
                break;
            }
            String ident = utvalgteIdenter.remove(0);
            List<Institusjonsforholdsmelding> eksisterendeInstitusjonsforhold =
                    identService.hentInstitusjonsoppholdFraInst2(tokenObject, ident);
            if (!eksisterendeInstitusjonsforhold.isEmpty()) {
                log.warn("Ident {} har allerede fått opprettet institusjonsforhold. Hopper over opprettelse.", ident);
            } else {
                institusjonsforholdsmelding.setPersonident(ident);
                ResponseEntity response = inst2Consumer.leggTilInstitusjonsoppholdIInst2(tokenObject, institusjonsforholdsmelding);
                responseEntities.add(ResponseEntity.status(response.getStatusCode()).body(response.getBody()));
                if (response.getStatusCode().is2xxSuccessful()) {
                    antallOppholdOpprettet++;
                    historikkSomSkalLagres.add(institusjonsforholdsmelding);
                }
            }
        }

        List<IdentMedData> identerMedData = new ArrayList<>(historikkSomSkalLagres.size());
        for (Institusjonsforholdsmelding institusjonsforholdsmelding : historikkSomSkalLagres) {
            identerMedData.add(new IdentMedData(institusjonsforholdsmelding.getPersonident(), Collections.singletonList(institusjonsforholdsmelding)));
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

        return responseEntities;
    }

    private List<Institusjonsforholdsmelding> validerOgFjernUgyldigeMeldinger(Map<String, Object> tokenObject, List<Institusjonsforholdsmelding> syntetiserteMeldinger) {
        List<Institusjonsforholdsmelding> gyldigeSyntetiserteMeldinger = new ArrayList<>(syntetiserteMeldinger.size());

        for (Institusjonsforholdsmelding melding : syntetiserteMeldinger) {
            String tssEksternId = melding.getTssEksternId();
            String startdato = melding.getStartdato();
            String faktiskSluttdato = melding.getFaktiskSluttdato();
            if (inst2Consumer.finnesInstitusjonPaaDato(tokenObject, tssEksternId, startdato).is2xxSuccessful()
                    && inst2Consumer.finnesInstitusjonPaaDato(tokenObject, tssEksternId, faktiskSluttdato).is2xxSuccessful()) {
                gyldigeSyntetiserteMeldinger.add(melding);
            }
        }

        return gyldigeSyntetiserteMeldinger;
    }
}
