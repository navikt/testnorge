package no.nav.registre.testnorge.synt.tiltakservice.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.request.RettighetRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.request.RettighetTiltaksdeltakelseRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.request.SyntRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

import static no.nav.registre.testnorge.domain.dto.arena.testnorge.tiltak.Constants.getDeltakerstatuskoderMedAarsakkoder;


@Service
@Slf4j
@RequiredArgsConstructor
public class RequestService {

    private final Random rand;

    static final String BEGRUNNELSE = "Syntetisert rettighet";

    SyntRequest createSyntRequest(LocalDate fraDato, LocalDate tilDato) {
        return SyntRequest.builder()
                .fraDato(fraDato.toString())
                .tilDato(tilDato.toString())
                .utfall("JA")
                .vedtakTypeKode("O")
                .vedtakDato(fraDato.toString())
                .build();

    }

    RettighetRequest getTiltaksdeltakelseRequest(String ident, String miljoe, NyttVedtakTiltak tiltak) {
        var nyTiltaksdeltakelse = NyttVedtakTiltak.builder()
                .tiltakId(tiltak.getTiltakId())
                .build();
        nyTiltaksdeltakelse.setTilDato(tiltak.getTilDato());
        nyTiltaksdeltakelse.setFraDato(tiltak.getFraDato());
        nyTiltaksdeltakelse.setBegrunnelse(BEGRUNNELSE);

        return getTiltakRequest(ident, miljoe, nyTiltaksdeltakelse);
    }

    RettighetRequest getEndreDeltakerstatusRequest(String ident, String miljoe, String status, NyttVedtakTiltak tiltak) {
        var nyEndring = NyttVedtakTiltak.builder()
                .deltakerstatusKode(status)
                .tiltakId(tiltak.getTiltakId())
                .build();
        nyEndring.setTilDato(tiltak.getTilDato());
        nyEndring.setFraDato(tiltak.getFraDato());

        if (getDeltakerstatuskoderMedAarsakkoder().containsKey(status)) {
            List<String> aarsakkoder = getDeltakerstatuskoderMedAarsakkoder().get(status);
            String aarsakkode = aarsakkoder.get(rand.nextInt(aarsakkoder.size()));
            nyEndring.setAarsakKode(aarsakkode);
        }

        return getTiltakRequest(ident, miljoe, nyEndring);
    }

    RettighetRequest getTiltakRequest(String ident, String miljoe, NyttVedtakTiltak tiltak) {
        var rettighetRequest = new RettighetTiltaksdeltakelseRequest(Collections.singletonList(tiltak));

        rettighetRequest.setPersonident(ident);
        rettighetRequest.setMiljoe(miljoe);
        return rettighetRequest;
    }

}
