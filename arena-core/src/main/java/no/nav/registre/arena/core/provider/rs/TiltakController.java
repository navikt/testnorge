package no.nav.registre.arena.core.provider.rs;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import no.nav.registre.arena.core.provider.rs.request.SyntetiserArenaRequest;
import no.nav.registre.arena.core.service.RettighetTiltakService;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class TiltakController {

    private final RettighetTiltakService rettighetTiltakService;

    @PostMapping("generer/tiltaksdeltakelse")
    public Map<String, List<NyttVedtakResponse>> genererTiltaksdeltakelse(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTiltakService.opprettTiltaksdeltakelse(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/tiltakspenger")
    public Map<String, List<NyttVedtakResponse>> genererTiltakspenger(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTiltakService.opprettTiltakspenger(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/barnetillegg")
    public Map<String, List<NyttVedtakResponse>> genererBarnetillegg(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetTiltakService.opprettBarnetillegg(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }
}
