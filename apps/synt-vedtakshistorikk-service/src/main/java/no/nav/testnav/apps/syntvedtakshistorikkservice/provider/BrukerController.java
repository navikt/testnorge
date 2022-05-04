package no.nav.testnav.apps.syntvedtakshistorikkservice.provider;

import lombok.RequiredArgsConstructor;

import no.nav.testnav.apps.syntvedtakshistorikkservice.provider.request.SyntetiserArenaRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.BrukerService;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.provider.utils.InputValidator.validateMiljoe;

@RestController
@RequestMapping("api/v1/generer")
@RequiredArgsConstructor
public class BrukerController {

    private final BrukerService brukerService;

    @PostMapping("/bruker/oppfoelging")
    public ResponseEntity<Map<String, NyeBrukereResponse>> registrerBrukereIArenaForvalterMedOppfoelging(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        validateMiljoe(syntetiserArenaRequest.getMiljoe());
        return ResponseEntity.ok().body(brukerService.registrerArenaBrukereMedOppfoelging(syntetiserArenaRequest));
    }

}
