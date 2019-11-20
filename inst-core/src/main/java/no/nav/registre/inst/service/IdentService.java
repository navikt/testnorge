package no.nav.registre.inst.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import no.nav.registre.inst.Institusjonsopphold;
import no.nav.registre.inst.consumer.rs.Inst2Consumer;
import no.nav.registre.inst.provider.rs.responses.OppholdResponse;

@Service
public class IdentService {

    @Autowired
    private Inst2Consumer inst2Consumer;

    @Autowired
    private Inst2FasitService inst2FasitService;

    public List<OppholdResponse> opprettInstitusjonsopphold(
            String callId,
            String consumerId,
            String miljoe,
            List<Institusjonsopphold> oppholdene
    ) {
        List<OppholdResponse> statusFraInst2 = new ArrayList<>(oppholdene.size());
        for (var opphold : oppholdene) {
            var oppholdResponse = sendTilInst2(callId, consumerId, miljoe, opphold);
            statusFraInst2.add(oppholdResponse);
        }
        return statusFraInst2;
    }

    public OppholdResponse sendTilInst2(
            String callId,
            String consumerId,
            String miljoe,
            Institusjonsopphold opphold
    ) {
        var oppholdResponse = inst2Consumer.leggTilInstitusjonsoppholdIInst2(
                hentTokenTilInst2(miljoe),
                callId,
                consumerId,
                miljoe,
                opphold);
        oppholdResponse.setPersonident(opphold.getPersonident());
        return oppholdResponse;
    }

    public List<OppholdResponse> slettInstitusjonsoppholdTilIdenter(
            String callId,
            String consumerId,
            String miljoe,
            List<String> identer
    ) {
        var tokenObject = hentTokenTilInst2(miljoe);

        List<OppholdResponse> sletteOppholdResponses = new ArrayList<>(identer.size());

        for (var ident : identer) {
            var institusjonsopphold = hentInstitusjonsoppholdFraInst2(tokenObject, callId, consumerId, miljoe, ident);
            var oppholdResponse = OppholdResponse.builder()
                    .personident(ident)
                    .build();
            if (institusjonsopphold.isEmpty()) {
                oppholdResponse.setStatus(HttpStatus.NOT_FOUND);
                oppholdResponse.setFeilmelding("Fant ingen institusjonsopphold på ident.");
            } else {
                for (var opphold : institusjonsopphold) {
                    var response = slettOppholdMedId(tokenObject, callId, consumerId, miljoe, opphold.getOppholdId());
                    if (response.getStatusCode().is2xxSuccessful()) {
                        oppholdResponse.setStatus(HttpStatus.OK);
                        oppholdResponse.setInstitusjonsopphold(opphold);
                        oppholdResponse.getInstitusjonsopphold().setPersonident(null); // finnes allerede i respons - unngå dobbel personident
                    } else {
                        oppholdResponse.setStatus(response.getStatusCode());
                        oppholdResponse.setFeilmelding(String.valueOf(response.getBody()));
                    }
                }
            }
            sletteOppholdResponses.add(oppholdResponse);
        }

        return sletteOppholdResponses;
    }

    public ResponseEntity oppdaterInstitusjonsopphold(
            String callId,
            String consumerId,
            String miljoe,
            Long oppholdId,
            Institusjonsopphold institusjonsopphold
    ) {
        var tokenObject = hentTokenTilInst2(miljoe);
        return inst2Consumer.oppdaterInstitusjonsoppholdIInst2(tokenObject, callId, consumerId, miljoe, oppholdId, institusjonsopphold);
    }

    public ResponseEntity slettOppholdMedId(
            Map<String, Object> tokenObject,
            String callId,
            String consumerId,
            String miljoe,
            Long oppholdId
    ) {
        return inst2Consumer.slettInstitusjonsoppholdFraInst2(tokenObject, callId, consumerId, miljoe, oppholdId);
    }

    public List<Institusjonsopphold> hentOppholdTilIdenter(
            String callId,
            String consumerId,
            String miljoe,
            List<String> identer
    ) {
        var tokenObject = hentTokenTilInst2(miljoe);
        List<Institusjonsopphold> alleInstitusjonsopphold = new ArrayList<>();
        for (var ident : identer) {
            var institusjonsoppholdTilIdent = hentInstitusjonsoppholdFraInst2(tokenObject, callId, consumerId, miljoe, ident);
            alleInstitusjonsopphold.addAll(institusjonsoppholdTilIdent);
        }
        return alleInstitusjonsopphold;
    }

    public List<Institusjonsopphold> hentInstitusjonsoppholdFraInst2(
            Map<String, Object> tokenObject,
            String callId,
            String consumerId,
            String miljoe,
            String ident
    ) {
        var institusjonsforholdsmeldinger = inst2Consumer.hentInstitusjonsoppholdFraInst2(tokenObject, callId, consumerId, miljoe, ident);
        if (institusjonsforholdsmeldinger != null) {
            for (var melding : institusjonsforholdsmeldinger) {
                melding.setPersonident(ident);
            }
            return new ArrayList<>(institusjonsforholdsmeldinger);
        } else {
            return new ArrayList<>();
        }
    }

    public Map<String, Object> hentTokenTilInst2(
            String miljoe
    ) {
        return inst2Consumer.hentTokenTilInst2(inst2FasitService.getFregTokenProviderInEnvironment(miljoe));
    }
}
