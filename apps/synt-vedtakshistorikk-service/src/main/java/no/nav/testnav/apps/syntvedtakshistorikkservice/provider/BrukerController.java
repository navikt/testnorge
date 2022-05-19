package no.nav.testnav.apps.syntvedtakshistorikkservice.provider;

import lombok.RequiredArgsConstructor;

import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.DagpengerResponseDTO;
import no.nav.testnav.apps.syntvedtakshistorikkservice.provider.request.SyntetiserArenaRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.ArenaDagpengerService;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.BrukerService;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.provider.utils.InputValidator.validateMiljoe;

@RestController
@RequestMapping("api/v1/bruker")
@RequiredArgsConstructor
public class BrukerController {

    private final BrukerService brukerService;
    private final ArenaDagpengerService arenaDagpengerService;

    @PostMapping("/oppfoelging")
    public Map<String, NyeBrukereResponse> registrerBrukereIArenaForvalterMedOppfoelging(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        validateMiljoe(syntetiserArenaRequest.getMiljoe());
        return brukerService.registrerArenaBrukereMedOppfoelging(syntetiserArenaRequest);
    }

    @PostMapping("/dagpenger")
    public Map<String, List<DagpengerResponseDTO>> registrerBrukereIArenaMedDagpenger(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return arenaDagpengerService.registrerArenaBrukereMedDagpenger(syntetiserArenaRequest.getAntallNyeIdenter(), syntetiserArenaRequest.getMiljoe());
    }

}
