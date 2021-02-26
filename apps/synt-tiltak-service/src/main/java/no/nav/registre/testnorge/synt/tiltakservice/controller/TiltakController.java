package no.nav.registre.testnorge.synt.tiltakservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import no.nav.registre.testnorge.synt.tiltakservice.controller.request.FinnTiltakRequest;
import no.nav.registre.testnorge.synt.tiltakservice.controller.request.TiltakspengerRequest;
import no.nav.registre.testnorge.synt.tiltakservice.controller.response.TiltakspengerResponse;
import no.nav.registre.testnorge.synt.tiltakservice.service.TiltakService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TiltakController {

    private final TiltakService tiltakService;

    @PostMapping("/finnTiltak")
    @Operation(summary = "Finn passende tiltak for ident.")
    public NyttVedtakTiltak finnTiltakForIdent(
            @RequestBody FinnTiltakRequest finnTiltakRequest
    ) {
        return tiltakService.finnTiltakForTiltakdeltakelse(finnTiltakRequest);
    }

    @PostMapping("/tiltakspenger")
    @Operation(summary = "Opprett syntetisk tiltakspenger vedtak med tilknyttet tiltaksdeltakelse.")
    public TiltakspengerResponse opprettTiltakspenger(
            @RequestBody TiltakspengerRequest request
    ) {
        return tiltakService.opprettTiltakspengerMedDeltakelse(request.getIdent(), request.getMiljoe(), request.getTiltak());
    }
}
