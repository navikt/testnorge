package no.nav.registre.inst.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.inst.Institusjonsopphold;
import no.nav.registre.inst.consumer.rs.Inst2Consumer;
import no.nav.registre.inst.provider.rs.responses.OppholdResponse;
import no.nav.registre.inst.provider.rs.responses.SletteOppholdResponse;

@Service
public class IdentService {

    @Autowired
    private Inst2Consumer inst2Consumer;

    public Map<String, List<OppholdResponse>> opprettInstitusjonsopphold(List<Institusjonsopphold> oppholdene, String callId, String consumerId) {
        Map<String, List<OppholdResponse>> statusFraInst2 = new HashMap<>();
        for (Institusjonsopphold opphold : oppholdene) {
            OppholdResponse oppholdResponse = sendTilInst2(opphold, callId, consumerId);
            String personident = opphold.getPersonident();
            if (statusFraInst2.containsKey(personident)) {
                statusFraInst2.get(personident).add(oppholdResponse);
            } else {
                List<OppholdResponse> oppholdResponses = new ArrayList<>();
                oppholdResponses.add(oppholdResponse);
                statusFraInst2.put(personident, oppholdResponses);
            }
        }
        return statusFraInst2;
    }

    public OppholdResponse sendTilInst2(Institusjonsopphold opphold, String callId, String consumerId) {
        return inst2Consumer.leggTilInstitusjonsoppholdIInst2(inst2Consumer.hentTokenTilInst2(), opphold, callId, consumerId);
    }

    public SletteOppholdResponse slettInstitusjonsforholdTilIdenter(List<String> identer, String callId, String consumerId) {
        Map<String, Object> tokenObject = hentTokenTilInst2();

        SletteOppholdResponse sletteOppholdResponse = SletteOppholdResponse.builder()
                .identerMedOppholdIdSomIkkeKunneSlettes(new HashMap<>())
                .identerMedOppholdIdSomBleSlettet(new HashMap<>())
                .build();

        for (String ident : identer) {
            List<Institusjonsopphold> institusjonsforholdsmeldinger = hentInstitusjonsoppholdFraInst2(tokenObject, ident, callId, consumerId);
            for (Institusjonsopphold melding : institusjonsforholdsmeldinger) {
                ResponseEntity response = slettOppholdMedId(tokenObject, callId, consumerId, melding.getOppholdId());
                if (response.getStatusCode().is2xxSuccessful()) {
                    leggTilIdentMedOppholdIResponse(sletteOppholdResponse.getIdenterMedOppholdIdSomBleSlettet(), ident, melding.getOppholdId());
                } else {
                    leggTilIdentMedOppholdIResponse(sletteOppholdResponse.getIdenterMedOppholdIdSomIkkeKunneSlettes(), ident, melding.getOppholdId());
                }
            }
        }

        return sletteOppholdResponse;
    }

    public ResponseEntity oppdaterInstitusjonsopphold(String callId, String consumerId, Long oppholdId, Institusjonsopphold institusjonsopphold) {
        Map<String, Object> tokenObject = hentTokenTilInst2();
        return inst2Consumer.oppdaterInstitusjonsoppholdIInst2(tokenObject, oppholdId, institusjonsopphold, callId, consumerId);
    }

    public ResponseEntity slettOppholdMedId(Map<String, Object> tokenObject, String callId, String consumerId, Long oppholdId) {
        return inst2Consumer.slettInstitusjonsoppholdFraInst2(tokenObject, oppholdId, callId, consumerId);
    }

    public Map<String, List<Institusjonsopphold>> hentOppholdTilIdenter(List<String> identer, String callId, String consumerId) {
        Map<String, Object> tokenObject = hentTokenTilInst2();
        return identer.parallelStream()
                .collect(Collectors.toMap(fnr -> fnr, fnr -> hentInstitusjonsoppholdFraInst2(tokenObject, fnr, callId, consumerId)));
    }

    public List<Institusjonsopphold> hentInstitusjonsoppholdFraInst2(Map<String, Object> tokenObject, String ident, String callId, String consumerId) {
        List<Institusjonsopphold> institusjonsforholdsmeldinger = inst2Consumer.hentInstitusjonsoppholdFraInst2(tokenObject, ident, callId, consumerId);
        if (institusjonsforholdsmeldinger != null) {
            for (Institusjonsopphold melding : institusjonsforholdsmeldinger) {
                melding.setPersonident(ident);
            }
            return new ArrayList<>(institusjonsforholdsmeldinger);
        } else {
            return new ArrayList<>();
        }
    }

    public Map<String, Object> hentTokenTilInst2() {
        return inst2Consumer.hentTokenTilInst2();
    }

    private void leggTilIdentMedOppholdIResponse(Map<String, List<Long>> identMedOpphold, String ident, Long oppholdId) {
        if (identMedOpphold.containsKey(ident)) {
            identMedOpphold.get(ident).add(oppholdId);
        } else {
            identMedOpphold.put(ident, new ArrayList<>(Collections.singletonList(oppholdId)));
        }
    }
}
