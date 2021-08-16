package no.nav.registre.inst.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inst.consumer.rs.Inst2Consumer;
import no.nav.registre.inst.domain.InstitusjonResponse;
import no.nav.registre.inst.domain.InstitusjonsoppholdV2;
import no.nav.registre.inst.exception.UkjentMiljoeException;
import no.nav.registre.inst.provider.rs.responses.OppholdResponse;
import no.nav.registre.inst.security.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdentService {

    private static final String KILDE = "Dolly";

    private final Inst2Consumer inst2Consumer;
    private final TokenService tokenService;


    public List<OppholdResponse> opprettInstitusjonsopphold(
            String callId,
            String consumerId,
            String miljoe,
            List<InstitusjonsoppholdV2> oppholdene
    ) {
        List<OppholdResponse> statusFraInst2 = new ArrayList<>(oppholdene.size());
        for (var opphold : oppholdene) {
            opphold.setRegistrertAv(KILDE);
            var oppholdResponse = sendTilInst2(callId, consumerId, miljoe, opphold);
            statusFraInst2.add(oppholdResponse);
        }
        return statusFraInst2;
    }

    public OppholdResponse sendTilInst2(
            String callId,
            String consumerId,
            String miljoe,
            InstitusjonsoppholdV2 opphold
    ) {
        log.info("Sender institusjonsopphold til inst2: " + opphold);
        var oppholdResponse = inst2Consumer.leggTilInstitusjonsoppholdIInst2(
                callId,
                consumerId,
                miljoe,
                opphold);
        oppholdResponse.setPersonident(opphold.getNorskident());
        return oppholdResponse;
    }

    public List<OppholdResponse> slettInstitusjonsoppholdTilIdenter(
            String callId,
            String consumerId,
            String miljoe,
            List<String> identer
    ) {
        List<OppholdResponse> sletteOppholdResponses = new ArrayList<>(identer.size());

        for (var ident : identer) {
            var response = slettOppholdMedIdent(callId, consumerId, miljoe, ident);
            sletteOppholdResponses.add(OppholdResponse.builder()
                    .personident(ident)
                    .status(response.getStatusCode())
                    .feilmelding(response.hasBody() ? String.valueOf(response.getBody()) : null)
                    .build());
        }

        return sletteOppholdResponses;
    }

    public ResponseEntity<Object> slettOppholdMedIdent(
            String callId,
            String consumerId,
            String miljoe,
            String ident
    ) {
        return inst2Consumer.slettInstitusjonsoppholdMedIdent(callId, consumerId, miljoe, ident);
    }

    public List<InstitusjonsoppholdV2> hentOppholdTilIdenter(
            String callId,
            String consumerId,
            String miljoe,
            List<String> identer
    ) {
        List<InstitusjonsoppholdV2> alleInstitusjonsopphold = new ArrayList<>();
        for (var ident : identer) {
            var institusjonsoppholdTilIdent = hentInstitusjonsoppholdFraInst2(callId, consumerId, miljoe, ident);
            alleInstitusjonsopphold.addAll(institusjonsoppholdTilIdent);
        }
        return alleInstitusjonsopphold;
    }

    public List<InstitusjonsoppholdV2> hentInstitusjonsoppholdFraInst2(
            String callId,
            String consumerId,
            String miljoe,
            String ident
    ) {
        var institusjonsforholdsmeldinger = inst2Consumer.hentInstitusjonsoppholdFraInst2(callId, consumerId, miljoe, ident);
        if (institusjonsforholdsmeldinger != null) {
            var opphold = getOppholdForMiljoe(institusjonsforholdsmeldinger, miljoe);
            for (var melding : opphold) {
                if (melding != null) {
                    melding.setNorskident(ident);
                }
            }
            return opphold;
        } else {
            return new ArrayList<>();
        }
    }

    public String hentTokenTilInst2(String miljoe) {
        if (miljoe.contains("q")) {
            return tokenService.getIdTokenQ();
        } else if (miljoe.contains("t")) {
            return tokenService.getIdTokenT();
        } else {
            throw new UkjentMiljoeException("Kjente ikke igjen miljø " + miljoe);
        }
    }

    public List<String> hentTilgjengeligeMiljoer() {
        return inst2Consumer.hentInst2TilgjengeligeMiljoer();
    }

    private List<InstitusjonsoppholdV2> getOppholdForMiljoe(InstitusjonResponse response, String miljoe) {
        if ("q1".equals(miljoe)) {
            return response.getQ1();
        }
        if ("q2".equals(miljoe)) {
            return response.getQ2();
        }
        if ("q4".equals(miljoe)) {
            return response.getQ4();
        }
        if ("t0".equals(miljoe)) {
            return response.getT0();
        }
        if ("t4".equals(miljoe)) {
            return response.getT4();
        }
        if ("t6".equals(miljoe)) {
            return response.getT6();
        } else return Collections.emptyList();
    }
}
