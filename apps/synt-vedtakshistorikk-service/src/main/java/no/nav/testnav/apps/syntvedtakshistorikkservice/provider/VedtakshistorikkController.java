package no.nav.testnav.apps.syntvedtakshistorikkservice.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/generer")
@RequiredArgsConstructor
public class VedtakshistorikkController {

    @GetMapping("/vedtakshistorikk")
    public Mono<ResponseEntity<Map<String, List<NyttVedtakResponse>>>> genererVedtakshistorikk() {
        return null;
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(vedtakshistorikkService.genererVedtakshistorikk(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter()));
    }
}
