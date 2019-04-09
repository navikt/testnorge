package no.nav.registre.inst.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import no.nav.registre.inst.consumer.rs.HodejegerenConsumer;
import no.nav.registre.inst.consumer.rs.Inst2Consumer;
import no.nav.registre.inst.consumer.rs.InstSyntetisererenConsumer;
import no.nav.registre.inst.institusjonsforhold.Institusjonsforholdsmelding;
import no.nav.registre.inst.provider.rs.requests.SyntetiserInstRequest;

@Service
@Slf4j
public class SyntetiseringService {

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
        List<Institusjonsforholdsmelding> syntetiserteMeldinger = instSyntetisererenConsumer.hentInstMeldingerFromSyntRest(utvalgteIdenter.size());
        return leggTilSyntetisertInstitusjonsoppholdIInst2(utvalgteIdenter, syntetiserteMeldinger);
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

    private List<ResponseEntity> leggTilSyntetisertInstitusjonsoppholdIInst2(List<String> identer, List<Institusjonsforholdsmelding> syntetiserteMeldinger) {
        Map<String, Object> tokenObject = hentTokenTilInst2();
        List<ResponseEntity> responseEntities = new ArrayList<>();
        List<String> utvalgteIdenter = new ArrayList<>(identer);

        for (Institusjonsforholdsmelding institusjonsforholdsmelding : syntetiserteMeldinger) {
            if (utvalgteIdenter.isEmpty()) {
                break;
            }
            String ident = utvalgteIdenter.remove(0);
            List<Institusjonsforholdsmelding> eksisterendeInstitusjonsforhold = hentInstitusjonsoppholdFraInst2(ident);
            if (!eksisterendeInstitusjonsforhold.isEmpty()) {
                log.warn("Ident {} har allerede fått opprettet institusjonsforhold. Hopper over opprettelse.", ident);
            } else {
                institusjonsforholdsmelding.setPersonident(ident);
                responseEntities.add(inst2Consumer.leggTilInstitusjonsoppholdIInst2(tokenObject, institusjonsforholdsmelding));
            }
        }

        if (responseEntities.size() < identer.size()) {
            log.warn("Kunne ikke opprette institusjonsopphold på alle identer. Antall opphold opprettet: {}", responseEntities.size());
        }

        return responseEntities;
    }

    private List<Institusjonsforholdsmelding> hentInstitusjonsoppholdFraInst2(String ident) {
        Map<String, Object> tokenObject = hentTokenTilInst2();
        List<Institusjonsforholdsmelding> institusjonsforholdsmeldinger = inst2Consumer.hentInstitusjonsoppholdFraInst2(tokenObject, ident);
        for (Institusjonsforholdsmelding melding : institusjonsforholdsmeldinger) {
            melding.setPersonident(ident);
        }
        return new ArrayList<>(institusjonsforholdsmeldinger);
    }

    private Map<String, Object> hentTokenTilInst2() {
        return inst2Consumer.hentTokenTilInst2();
    }
}
