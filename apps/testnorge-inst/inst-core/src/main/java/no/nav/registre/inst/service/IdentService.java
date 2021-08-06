package no.nav.registre.inst.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inst.Institusjonsopphold;
import no.nav.registre.inst.InstitusjonsoppholdV2;
import no.nav.registre.inst.consumer.rs.Inst2Consumer;
import no.nav.registre.inst.exception.UkjentMiljoeException;
import no.nav.registre.inst.provider.rs.responses.OppholdResponse;
import no.nav.registre.inst.security.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdentService {

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
                hentTokenTilInst2(miljoe),
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
        var bearerToken = hentTokenTilInst2(miljoe);

        List<OppholdResponse> sletteOppholdResponses = new ArrayList<>(identer.size());

        for (var ident : identer) {
            var response = slettOppholdMedIdent(bearerToken, callId, consumerId, miljoe, ident);
            sletteOppholdResponses.add(OppholdResponse.builder()
                    .personident(ident)
                    .status(response.getStatusCode())
                    .feilmelding(response.hasBody() ? String.valueOf(response.getBody()) : null)
                    .build());
        }

        return sletteOppholdResponses;
    }

    public ResponseEntity<Object> oppdaterInstitusjonsopphold(
            String callId,
            String consumerId,
            String miljoe,
            Long oppholdId,
            Institusjonsopphold institusjonsopphold
    ) {
        var bearerToken = hentTokenTilInst2(miljoe);
        return inst2Consumer.oppdaterInstitusjonsoppholdIInst2(bearerToken, callId, consumerId, miljoe, oppholdId, institusjonsopphold);
    }

    public ResponseEntity<Object> slettOppholdMedIdent(
            String bearerToken,
            String callId,
            String consumerId,
            String miljoe,
            String ident
    ) {
        return inst2Consumer.slettInstitusjonsoppholdMedIdent(bearerToken, callId, consumerId, miljoe, ident);
    }

    public List<Institusjonsopphold> hentOppholdTilIdenter(
            String callId,
            String consumerId,
            String miljoe,
            List<String> identer
    ) {
        var bearerToken = hentTokenTilInst2(miljoe);
        List<Institusjonsopphold> alleInstitusjonsopphold = new ArrayList<>();
        for (var ident : identer) {
            var institusjonsoppholdTilIdent = hentInstitusjonsoppholdFraInst2(bearerToken, callId, consumerId, miljoe, ident);
            alleInstitusjonsopphold.addAll(institusjonsoppholdTilIdent);
        }
        return alleInstitusjonsopphold;
    }

    public List<Institusjonsopphold> hentInstitusjonsoppholdFraInst2(
            String bearerToken,
            String callId,
            String consumerId,
            String miljoe,
            String ident
    ) {
        var institusjonsforholdsmeldinger = inst2Consumer.hentInstitusjonsoppholdFraInst2(bearerToken, callId, consumerId, miljoe, ident);
        if (institusjonsforholdsmeldinger != null) {
            for (var melding : institusjonsforholdsmeldinger) {
                melding.setPersonident(ident);
            }
            return new ArrayList<>(institusjonsforholdsmeldinger);
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

    public List<String> hentTilgjengeligeMiljoer(List<String> aktuelleMiljoer) {
        List<String> tilgjengeligeMiljoer = new ArrayList<>();
        for (String miljoe : aktuelleMiljoer) {
            if (inst2Consumer.isMiljoeTilgjengelig(miljoe)) {
                tilgjengeligeMiljoer.add(miljoe);
            }
        }
        return tilgjengeligeMiljoer;
    }
}
