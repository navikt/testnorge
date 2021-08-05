package no.nav.registre.testnorge.arena.provider.rs;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.arena.provider.rs.request.SyntetiserArenaRequest;
import no.nav.registre.testnorge.arena.service.VedtakshistorikkService;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class VedtakshistorikkController {

    private final VedtakshistorikkService vedtakshistorikkService;

    @PostMapping("generer/vedtakshistorikk")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererVedtakshistorikk(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(vedtakshistorikkService.genererVedtakshistorikk(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }
}
