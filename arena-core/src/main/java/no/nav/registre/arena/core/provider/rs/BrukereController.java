package no.nav.registre.arena.core.provider.rs;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.arena.domain.vedtak.NyeBrukereResponse;
import no.nav.registre.arena.core.provider.rs.request.SyntetiserArenaRequest;
import no.nav.registre.arena.core.service.BrukereService;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class BrukereController {

    private final BrukereService brukereService;

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
        var respons = brukereService.opprettArbeidsoekere(
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
        var response = brukereService.opprettArbeidssoeker(
                personident,
                arenaRequest.getAvspillergruppeId(),
                arenaRequest.getMiljoe()
        );

        return ResponseEntity.ok().body(response);
    }
}
