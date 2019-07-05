package no.nav.registre.arena.core.provider.rs;


import no.nav.registre.arena.core.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.arena.core.service.SyntetiseringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringController {
    @Autowired
    SyntetiseringService syntetiseringService;

    @PostMapping(value = "/generer")
    public ResponseEntity registerBrukereIArenaForvalter(@RequestBody SyntetiserArenaRequest syntetiserArenaRequest,
                                                         @RequestParam Integer antallNyeIdenter) {
        if (antallNyeIdenter != null)
            return syntetiseringService.registrerBrukereIArenaForvalter(syntetiserArenaRequest, antallNyeIdenter);
        return syntetiseringService.fyllOppBrukereIArenaForvalter(syntetiserArenaRequest);
    }

    @PostMapping(value = "/slett")
    public ResponseEntity slettBrukereIArenaForvalter(@RequestBody SyntetiserArenaRequest syntetiserArenaRequest,
                                                      @RequestParam String personident) {
        return syntetiseringService.slettBrukereIArenaForvalter(syntetiserArenaRequest, personident);
    }
}
