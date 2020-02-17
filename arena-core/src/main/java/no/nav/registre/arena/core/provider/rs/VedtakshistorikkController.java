package no.nav.registre.arena.core.provider.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.arena.core.provider.rs.request.SyntetiserArenaRequest;
import no.nav.registre.arena.core.service.VedtakshistorikkService;
import no.nav.registre.arena.domain.vedtak.NyttVedtakResponse;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class VedtakshistorikkController {

    private final VedtakshistorikkService vedtakshistorikkService;

    @PostMapping("generer/vedtakshistorikk")
    public List<NyttVedtakResponse> genererVedtakshistorikk(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return vedtakshistorikkService.genererVedtakshistorikk(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }
}
