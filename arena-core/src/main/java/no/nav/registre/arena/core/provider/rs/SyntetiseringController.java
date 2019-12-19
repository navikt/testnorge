package no.nav.registre.arena.core.provider.rs;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.arena.core.consumer.rs.responses.NyeBrukereResponse;
import no.nav.registre.arena.core.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.arena.core.service.RettighetService;
import no.nav.registre.arena.core.service.SyntetiseringService;
import no.nav.registre.arena.domain.rettighet.NyRettighetResponse;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class SyntetiseringController {

    private final SyntetiseringService syntetiseringService;
    private final RettighetService rettighetService;

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

    @PostMapping("generer/rettighet/aap")
    public List<NyRettighetResponse> genererRettighetAap(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetService.genererAap(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/rettighet/ungUfoer")
    public List<NyRettighetResponse> genererRettighetUngUfoer(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetService.genererUngUfoer(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/rettighet/tvungenForvaltning")
    public List<NyRettighetResponse> genererRettighetTvungenForvaltning(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetService.genererTvungenForvaltning(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    @PostMapping("generer/rettighet/fritakMeldekort")
    public List<NyRettighetResponse> genererRettighetFritakMeldekort(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return rettighetService.genererFritakMeldekort(syntetiserArenaRequest.getAvspillergruppeId(), syntetiserArenaRequest.getMiljoe(), syntetiserArenaRequest.getAntallNyeIdenter());
    }

    private ResponseEntity<NyeBrukereResponse> registrerBrukereIArenaForvalter(
            SyntetiserArenaRequest arenaRequest
    ) {
        var respons = syntetiseringService.opprettArbeidsoekere(
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
        var response = syntetiseringService.opprettArbeidssoeker(
                personident,
                arenaRequest.getAvspillergruppeId(),
                arenaRequest.getMiljoe()
        );

        return ResponseEntity.ok().body(response);
    }
}
