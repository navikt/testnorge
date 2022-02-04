package no.nav.testnav.apps.syntvedtakshistorikkservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.syntvedtakshistorikkservice.provider.request.SyntetiserArenaRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.VedtakshistorikkService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/generer")
@RequiredArgsConstructor
public class VedtakshistorikkController {

    private final VedtakshistorikkService vedtakshistorikkService;

    @PostMapping("/vedtakshistorikk")
    public ResponseEntity<Map<String, List<NyttVedtakResponse>>> genererVedtakshistorikk(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(vedtakshistorikkService.genererVedtakshistorikk(
                        syntetiserArenaRequest.getMiljoe(),
                        syntetiserArenaRequest.getAntallNyeIdenter()));
    }
}
