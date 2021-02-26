package no.nav.registre.testnorge.synt.tiltakservice.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Arbeidsoeker;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Kvalifiseringsgrupper;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.NyBruker;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.request.RettighetRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import no.nav.registre.testnorge.synt.tiltakservice.consumer.ArenaForvalterConsumer;
import no.nav.registre.testnorge.synt.tiltakservice.controller.request.FinnTiltakRequest;

import static no.nav.registre.testnorge.domain.dto.arena.testnorge.tiltak.Constants.getPlanlagtTiltakStatus;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArenaForvalterService {

    private final ArenaForvalterConsumer arenaForvalterConsumer;
    private final Random rand;

    Arbeidsoeker opprettTiltakArbeidssoekerIArena(
            String ident,
            String miljoe
    ) {
        var arbeidssoeker = hentArbeidssoeker(ident, miljoe);
        if (arbeidssoeker == null) {
            var response = sendArbeidssoekerTilArenaForvalter(ident, miljoe, rand.nextBoolean() ? Kvalifiseringsgrupper.BATT : Kvalifiseringsgrupper.VARIG);
            if (response != null && response.getArbeidsoekerList() != null && response.getArbeidsoekerList().isEmpty()) {
                return response.getArbeidsoekerList().get(0);
            }
        }
        return arbeidssoeker;
    }

    private Arbeidsoeker hentArbeidssoeker(
            String ident,
            String miljoe
    ) {
        var arbeidssoeker = arenaForvalterConsumer.hentDollyArbeidssoekerIArena(ident, miljoe);
        if (arbeidssoeker != null && arbeidssoeker.getArbeidsoekerList() != null && !arbeidssoeker.getArbeidsoekerList().isEmpty()) {
            return arbeidssoeker.getArbeidsoekerList().get(0);
        }
        return null;
    }

    private NyeBrukereResponse sendArbeidssoekerTilArenaForvalter(
            String ident,
            String miljoe,
            Kvalifiseringsgrupper kvalifiseringsgruppe
    ) {
        var nyBruker = Collections.singletonList(
                NyBruker.builder()
                        .personident(ident)
                        .miljoe(miljoe)
                        .kvalifiseringsgruppe(kvalifiseringsgruppe)
                        .automatiskInnsendingAvMeldekort(true)
                        .oppfolging("N")
                        .build());

        return arenaForvalterConsumer.sendTilArenaForvalter(nyBruker);
    }

    NyttVedtakTiltak finnTiltakForTiltaksdeltakelse(FinnTiltakRequest tiltak) {
        var response = arenaForvalterConsumer.finnTiltak(tiltak.getRettighetRequest());
        if (response != null && !response.getNyeRettigheterTiltak().isEmpty()) {
            var tiltakStatus = response.getNyeRettigheterTiltak().get(0).getTiltakStatusKode();
            if (tiltakStatus != null && !tiltakStatus.equals(getPlanlagtTiltakStatus())) {
                return response.getNyeRettigheterTiltak().get(0);
            }
        }

        log.info("Fant ikke tiltak for ident.");
        return null;
    }

    List<NyttVedtakResponse> opprettRettigheter(List<RettighetRequest> rettigheter) {
        List<NyttVedtakResponse> responses = new ArrayList<>();
        for (var rettighet : rettigheter) {
            var response = arenaForvalterConsumer.opprettRettighet(rettighet);
            responses.add(response);
            if (response == null || (response.getFeiledeRettigheter() != null && !response.getFeiledeRettigheter().isEmpty())) {
                log.info("Innsendt rettighet feilet. Stopper videre innsending av rettigheter for ident: "
                        + rettighet.getPersonident());
                break;
            }
        }
        return responses;
    }
}
