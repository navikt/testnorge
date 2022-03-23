package no.nav.testnav.apps.syntvedtakshistorikkservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.PdlProxyConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.provider.request.SyntetiserArenaRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.ArenaForvalterService;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.IdentService;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.MAKSIMUM_ALDER;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.MINIMUM_ALDER;

@RestController
@RequestMapping("api/v1/generer")
@RequiredArgsConstructor
public class BrukereController {

    private final ArenaForvalterService arenaForvalterService;
    private final IdentService identService;
    private final PdlProxyConsumer pdlProxyConsumer;

    @PostMapping("/bruker/oppfoelging")
//    @ApiOperation(value = "Legg til identer med oppfoelging i Arena", notes = "Legger til oppgitt antall identer i Arena med oppfoelging.")
    public ResponseEntity<Map<String, NyeBrukereResponse>> registrerBrukereIArenaForvalterMedOppfoelging(
            @RequestBody(required = false) SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        var identer = identService.getUtvalgteIdenterIAldersgruppe(
                syntetiserArenaRequest.getAntallNyeIdenter(),
                MINIMUM_ALDER,
                MAKSIMUM_ALDER,
                null);

        var response = arenaForvalterService.opprettArbeidssoekereUtenVedtak(
                identer,
                syntetiserArenaRequest.getMiljoe());

        pdlProxyConsumer.createSyntTags(identer);

        return ResponseEntity.ok().body(response);
    }

}
