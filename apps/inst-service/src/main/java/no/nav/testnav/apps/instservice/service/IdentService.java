package no.nav.testnav.apps.instservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.testnav.apps.instservice.consumer.InstTestdataConsumer;
import no.nav.testnav.apps.instservice.domain.InstitusjonResponse;
import no.nav.testnav.apps.instservice.domain.InstitusjonsoppholdV2;
import no.nav.testnav.apps.instservice.provider.responses.OppholdResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentService {

    private static final String KILDE = "Dolly";

    private final InstTestdataConsumer instTestdataConsumer;

    public List<OppholdResponse> opprettInstitusjonsopphold(
            String miljoe,
            List<InstitusjonsoppholdV2> oppholdene
    ) {
        List<OppholdResponse> statusFraInst2 = new ArrayList<>(oppholdene.size());
        for (var opphold : oppholdene) {
            opphold.setRegistrertAv(KILDE);
            var oppholdResponse = sendTilInst2(miljoe, opphold);
            statusFraInst2.add(oppholdResponse);
        }
        return statusFraInst2;
    }

    public OppholdResponse sendTilInst2(
            String miljoe,
            InstitusjonsoppholdV2 opphold
    ) {
        log.info("Sender institusjonsopphold til inst2: " + opphold);
        var oppholdResponse = instTestdataConsumer.leggTilInstitusjonsoppholdIInst2(miljoe, opphold);
        oppholdResponse.setPersonident(opphold.getNorskident());
        return oppholdResponse;
    }

    public List<OppholdResponse> slettInstitusjonsoppholdTilIdenter(
            String miljoe,
            List<String> identer
    ) {
        List<OppholdResponse> sletteOppholdResponses = new ArrayList<>(identer.size());

        for (var ident : identer) {
            var response = slettOppholdMedIdent(miljoe, ident);
            sletteOppholdResponses.add(OppholdResponse.builder()
                    .personident(ident)
                    .status(response.getStatusCode())
                    .feilmelding(response.hasBody() ? String.valueOf(response.getBody()) : null)
                    .build());
        }

        return sletteOppholdResponses;
    }

    public ResponseEntity<Object> slettOppholdMedIdent(String miljoe, String ident) {
        return instTestdataConsumer.slettInstitusjonsoppholdMedIdent(miljoe, ident);
    }

    public List<InstitusjonsoppholdV2> hentOppholdTilIdenter(String miljoe, List<String> identer) {
        List<InstitusjonsoppholdV2> alleInstitusjonsopphold = new ArrayList<>();
        for (var ident : identer) {
            var institusjonsoppholdTilIdent = hentInstitusjonsoppholdFraInst2(miljoe, ident);
            alleInstitusjonsopphold.addAll(institusjonsoppholdTilIdent);
        }
        return alleInstitusjonsopphold;
    }

    public List<InstitusjonsoppholdV2> hentInstitusjonsoppholdFraInst2(String miljoe, String ident) {
        var institusjonsforholdsmeldinger = instTestdataConsumer.hentInstitusjonsoppholdFraInst2(miljoe, ident);
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

    public List<String> hentTilgjengeligeMiljoer() {
        return instTestdataConsumer.hentInst2TilgjengeligeMiljoer();
    }

    private List<InstitusjonsoppholdV2> getOppholdForMiljoe(InstitusjonResponse response, String miljoe) {
        return switch (miljoe) {
            case "q1" -> response.getQ1();
            case "q2" -> response.getQ2();
            case "q4" -> response.getQ4();
            case "t0" -> response.getT0();
            case "t4" -> response.getT4();
            case "t6" -> response.getT6();
            default -> Collections.emptyList();
        };
    }
}
