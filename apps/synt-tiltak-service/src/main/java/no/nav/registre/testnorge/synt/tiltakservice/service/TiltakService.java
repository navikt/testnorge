package no.nav.registre.testnorge.synt.tiltakservice.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import no.nav.registre.testnorge.synt.tiltakservice.controller.request.FinnTiltakRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class TiltakService {

    private final ArenaForvalterService arenaForvalterService;

    public NyttVedtakTiltak finnTiltakForTiltakdeltakelse(FinnTiltakRequest request) {
        var arbeidssoeker = arenaForvalterService.opprettTiltakArbeidssoekerIArena(request.getFodselsnr(), request.getMiljoe());

        if (arbeidssoeker != null) {
            return arenaForvalterService.finnTiltakForIdent(request);
        } else {
            log.error("Kunne ikke opprette ident som arbeidssoeker i Arena");
            return null;
        }
    }

}
