package no.nav.registre.arena.core.provider.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.arena.core.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.arena.core.service.RettighetTiltakService;
import no.nav.registre.arena.domain.vedtak.NyttVedtakResponse;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class TiltakController {

    private final RettighetTiltakService rettighetTiltakService;

    @PostMapping("generer/tiltaksdeltakelse")
    public List<NyttVedtakResponse> genererTiltaksdeltakelse(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTiltakService.opprettTiltaksdeltakelse(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tiltakspenger")
    public List<NyttVedtakResponse> genererTiltakspenger(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTiltakService.opprettTiltakspenger(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/barnetillegg")
    public List<NyttVedtakResponse> genererBarnetillegg(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTiltakService.opprettBarnetillegg(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }
}
