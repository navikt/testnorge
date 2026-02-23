package no.nav.testnav.apps.syntvedtakshistorikkservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.syntvedtakshistorikkservice.provider.request.SyntetiserArenaRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.VedtakshistorikkService;
import no.nav.testnav.libs.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.provider.utils.InputValidator.validateMiljoe;

@RestController
@RequestMapping("api/v1/generer")
@RequiredArgsConstructor
public class VedtakshistorikkController {

    private final VedtakshistorikkService vedtakshistorikkService;

    @PostMapping("/vedtakshistorikk")
    public Mono<Map<String, List<NyttVedtakResponse>>> genererVedtakshistorikk(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        validateMiljoe(syntetiserArenaRequest.getMiljoe());
        return Mono.fromCallable(() -> vedtakshistorikkService.genererVedtakshistorikk(
                        syntetiserArenaRequest.getMiljoe(),
                        syntetiserArenaRequest.getAntallNyeIdenter()))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
