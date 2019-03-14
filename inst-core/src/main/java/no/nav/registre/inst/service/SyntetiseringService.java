package no.nav.registre.inst.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
        List<Institusjonsforholdsmelding> syntetiserteMeldinger = instSyntetisererenConsumer.hentInstMeldingerFromSyntRest(utvalgteIdenter.size());
        return leggTilSyntetisertInstitusjonsoppholdIInst2(utvalgteIdenter, syntetiserteMeldinger);
    }

    private List<String> finnLevendeIdenter(SyntetiserInstRequest syntetiserInstRequest) {
        List<String> alleLevendeIdenter = hodejegerenConsumer.finnLevendeIdenter(syntetiserInstRequest.getAvspillergruppeId());
        List<String> utvalgteIdenter = new ArrayList<>(syntetiserInstRequest.getAntallNyeIdenter());

        for (int i = 0; i < syntetiserInstRequest.getAntallNyeIdenter(); i++) {
            utvalgteIdenter.add(alleLevendeIdenter.remove(rand.nextInt(alleLevendeIdenter.size())));
        }

        return utvalgteIdenter;
    }

    private List<ResponseEntity> leggTilSyntetisertInstitusjonsoppholdIInst2(List<String> identer, List<Institusjonsforholdsmelding> syntetiserteMeldinger) {
        Map<String, Object> tokenObject = hentTokenTilInst2();
        List<ResponseEntity> responseEntities = new ArrayList<>();
        List<String> utvalgteIdenter = new ArrayList<>(identer);

        for (Institusjonsforholdsmelding institusjonsforholdsmelding : syntetiserteMeldinger) {
            String ident = utvalgteIdenter.remove(0);
            List<Institusjonsforholdsmelding> eksisterendeInstitusjonsforhold = hentInstitusjonsoppholdFraInst2(ident);
            if (eksisterendeInstitusjonsforhold.size() > 0) {
                log.warn("Ident {} har allerede fått opprettet institusjonsforhold. Hopper over opprettelse.", ident);
            } else {
                institusjonsforholdsmelding.setPersonident(ident);
                responseEntities.add(inst2Consumer.leggTilInstitusjonsoppholdIInst2(tokenObject, institusjonsforholdsmelding));
            }
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
