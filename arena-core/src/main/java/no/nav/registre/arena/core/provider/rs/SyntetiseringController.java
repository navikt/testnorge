package no.nav.registre.arena.core.provider.rs;

import no.nav.registre.arena.core.provider.rs.requests.SlettArenaRequest;
import no.nav.registre.arena.core.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.arena.core.service.SyntetiseringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringController {

    @Autowired
    SyntetiseringService syntetiseringService;

    @PostMapping(value = "/generer")
    public ResponseEntity<Integer> registerBrukereIArenaForvalter(@RequestBody SyntetiserArenaRequest syntetiserArenaRequest) {
        return registrerBrukereIArenaForvalter(syntetiserArenaRequest);
    }

    @PostMapping(value = "/slett")
    public ResponseEntity<Map<String, Integer>> slettBrukereIArenaForvalter(@RequestBody SlettArenaRequest slettArenaRequest) {
        return slettBrukere(slettArenaRequest);
    }


    private ResponseEntity<Integer> registrerBrukereIArenaForvalter(SyntetiserArenaRequest arenaRequest) {

            return ResponseEntity.ok(syntetiseringService.sendBrukereTilArenaForvalterConsumer(arenaRequest).size());
    }

    private ResponseEntity<Map<String, Integer>> slettBrukere(SlettArenaRequest slettArenaRequest) {

        Map<String, Integer> responseBody = new HashMap<>();

        List<String> slettedeIdenter = new ArrayList<>(syntetiseringService.slettBrukereIArenaForvalter(slettArenaRequest));
        responseBody.put("slettet", slettedeIdenter.size());

        List<String> alleIdenter = new ArrayList<>(slettArenaRequest.getIdenter());
        alleIdenter.removeAll(slettedeIdenter);
        responseBody.put("ikkeSlettet", alleIdenter.size());

        return ResponseEntity.ok(responseBody);
    }

}
