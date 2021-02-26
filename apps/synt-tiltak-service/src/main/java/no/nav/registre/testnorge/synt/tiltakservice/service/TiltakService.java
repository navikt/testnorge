package no.nav.registre.testnorge.synt.tiltakservice.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Deltakerstatuser;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.request.RettighetRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import no.nav.registre.testnorge.synt.tiltakservice.consumer.SyntTiltakConsumer;
import no.nav.registre.testnorge.synt.tiltakservice.controller.request.FinnTiltakRequest;
import no.nav.registre.testnorge.synt.tiltakservice.controller.response.TiltakspengerResponse;

import static no.nav.registre.testnorge.synt.tiltakservice.service.RequestService.BEGRUNNELSE;
import static no.nav.registre.testnorge.domain.dto.arena.testnorge.tiltak.Constants.getAvbruttTiltakStatuser;

@Service
@Slf4j
@RequiredArgsConstructor
public class TiltakService {

    private final ArenaForvalterService arenaForvalterService;
    private final SyntTiltakConsumer syntTiltakConsumer;
    private final RequestService requestService;

    private static final List<String> GYLDIGE_ADMINKODER = new ArrayList<>(Arrays.asList("AMO", "IND", "INST"));

    public NyttVedtakTiltak finnTiltakForTiltakdeltakelse(FinnTiltakRequest request) {
        var arbeidssoeker = arenaForvalterService.opprettTiltakArbeidssoekerIArena(request.getFodselsnr(), request.getMiljoe());

        if (arbeidssoeker != null) {
            return arenaForvalterService.finnTiltakForTiltaksdeltakelse(request);
        } else {
            log.error("Kunne ikke opprette ident som arbeidssoeker i Arena");
            return null;
        }
    }

    public TiltakspengerResponse opprettTiltakspengerMedDeltakelse(
            String ident,
            String miljoe,
            NyttVedtakTiltak tiltak
    ) {
        List<RettighetRequest> rettigheter = new ArrayList<>();

        rettigheter.add(requestService.getTiltaksdeltakelseRequest(ident, miljoe, tiltak));

        var endringerTilGjennomfoeres = getFoersteEndringerDeltakerstatus(tiltak.getTiltakAdminKode());

        for (var endring : endringerTilGjennomfoeres) {
            rettigheter.add(requestService.getEndreDeltakerstatusRequest(ident, miljoe, endring, tiltak));
        }

        var syntRequest = requestService.createSyntRequest(tiltak.getFraDato(), tiltak.getTilDato());
        var tiltakspenger = syntTiltakConsumer.getSyntetiskTiltakspenger(Collections.singletonList(syntRequest)).get(0);
        tiltakspenger.setBegrunnelse(BEGRUNNELSE);

        rettigheter.add(requestService.getTiltakRequest(ident, miljoe, tiltakspenger));

        var opprettedeRettigheter = arenaForvalterService.opprettRettigheter(rettigheter);

        updateDeltakelseTilFinished(tiltak, ident, miljoe);

        return TiltakspengerResponse.builder()
                .ident(ident)
                .miljoe(miljoe)
                .response(opprettedeRettigheter.get(opprettedeRettigheter.size() - 1))
                .build();

    }

    private void updateDeltakelseTilFinished(NyttVedtakTiltak tiltak, String ident, String miljoe) {
        if (canSetDeltakelseTilFinished(tiltak)) {
            var endring = getAvsluttendeDeltakerstatus(tiltak);
            var sisteEndringsRequest = requestService.getEndreDeltakerstatusRequest(ident, miljoe, endring, tiltak);
            arenaForvalterService.opprettRettigheter(Collections.singletonList(sisteEndringsRequest));
        }
    }

    private boolean canSetDeltakelseTilFinished(NyttVedtakTiltak tiltak) {
        var status = tiltak.getTiltakStatusKode();
        if (status != null && getAvbruttTiltakStatuser().contains(status)) {
            return true;
        }

        var fraDato = tiltak.getFraDato();
        var tilDato = tiltak.getTilDato();

        if (fraDato == null || tilDato == null) {
            return false;
        }
        return fraDato.isBefore(LocalDate.now().plusDays(1)) && tilDato.isBefore(LocalDate.now().plusDays(1));
    }

    private List<String> getFoersteEndringerDeltakerstatus(String adminkode) {
        if (GYLDIGE_ADMINKODER.contains(adminkode)) {
            var sisteEndring = Deltakerstatuser.GJENN.toString();
            if (adminkode.equals("AMO")) {
                return new ArrayList<>(Arrays.asList(Deltakerstatuser.TILBUD.toString(), Deltakerstatuser.JATAKK.toString(), sisteEndring));
            }
            return Collections.singletonList(sisteEndring);
        } else {
            log.info("Ugylding tiltak adminkode.");
            return new ArrayList<>();
        }
    }

    private String getAvsluttendeDeltakerstatus(NyttVedtakTiltak tiltak) {
        if (tiltak.getTiltakStatusKode() != null && getAvbruttTiltakStatuser().contains(tiltak.getTiltakStatusKode())) {
            return Deltakerstatuser.DELAVB.toString();
        }
        return Deltakerstatuser.FULLF.toString();
    }

}
