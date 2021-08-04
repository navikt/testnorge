package no.nav.registre.testnorge.arena.provider.rs;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.arena.provider.rs.request.SyntetiserArenaRequest;
import no.nav.registre.testnorge.arena.service.ArenaBrukerService;
import no.nav.registre.testnorge.arena.service.IdentService;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.MAKSIMUM_ALDER;
import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.MINIMUM_ALDER;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class BrukereController {

    private final ArenaBrukerService arenaBrukerService;
    private final IdentService identService;

    @PostMapping("/generer")
    @ApiOperation(value = "Legg til identer i Arena", notes = "Legger til oppgitt antall identer i Arena. Dersom ingen antall identer blir oppgitt fyller den opp slik at 20% tilgjengelige gyldige identer ligger i Arena. \nResponse: liste av opprettede identer.")
    public ResponseEntity<NyeBrukereResponse> registrerBrukereIArenaForvalter(
            @RequestParam(required = false) String personident,
            @RequestBody(required = false) SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        if (personident == null || personident.isEmpty()) {
            return registrerBrukereIArenaForvalter(syntetiserArenaRequest);
        }

        return registrerBrukerIArenaForvalter(personident, syntetiserArenaRequest);
    }

    private ResponseEntity<NyeBrukereResponse> registrerBrukereIArenaForvalter(
            SyntetiserArenaRequest arenaRequest
    ) {
        var respons = arenaBrukerService.opprettArbeidsoekere(
                arenaRequest.getAntallNyeIdenter(),
                arenaRequest.getAvspillergruppeId(),
                arenaRequest.getMiljoe()
        );

        return ResponseEntity.ok().body(respons);
    }

    private ResponseEntity<NyeBrukereResponse> registrerBrukerIArenaForvalter(
            String personident,
            SyntetiserArenaRequest arenaRequest
    ) {
        var response = arenaBrukerService.opprettArbeidssoeker(
                personident,
                arenaRequest.getAvspillergruppeId(),
                arenaRequest.getMiljoe(),
                false
        );

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/generer/oppfoelging")
    @ApiOperation(value = "Legg til identer med oppfoelging i Arena", notes = "Legger til oppgitt antall identer i Arena med oppfoelging.")
    public ResponseEntity<Map<String, NyeBrukereResponse>> registrerBrukereIArenaForvalterMedOppfoelging(
            @RequestBody(required = false) SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        var identer = identService.getUtvalgteIdenterIAldersgruppe(
                syntetiserArenaRequest.getAvspillergruppeId(),
                syntetiserArenaRequest.getAntallNyeIdenter(),
                MINIMUM_ALDER,
                MAKSIMUM_ALDER,
                syntetiserArenaRequest.getMiljoe(),
                null);

        var response = arenaBrukerService.opprettArbeidssoekereUtenVedtak(
                identer,
                syntetiserArenaRequest.getMiljoe());

        return ResponseEntity.ok().body(response);
    }

}
