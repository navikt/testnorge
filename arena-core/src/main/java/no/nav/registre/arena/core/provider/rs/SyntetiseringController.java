package no.nav.registre.arena.core.provider.rs;

import no.nav.registre.arena.core.consumer.rs.responses.Arbeidsoker;
import no.nav.registre.arena.core.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.arena.core.service.SyntetiseringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("api/v1/syntetisering")

public class SyntetiseringController {

    @Autowired
    SyntetiseringService syntetiseringService;

    @PostMapping(value = "/generer")
    public ResponseEntity<List<String>> registerBrukereIArenaForvalter(@RequestBody SyntetiserArenaRequest syntetiserArenaRequest) {
        return registrerBrukereIArenaForvalter(syntetiserArenaRequest);
    }

    @PostMapping(value = "/slett")
    public ResponseEntity<List<String>> slettBrukereIArenaForvalter(@RequestParam String miljoe, @RequestBody List<String> identer) {
        return slettBrukere(miljoe, identer);
    }


    private ResponseEntity<List<String>> registrerBrukereIArenaForvalter(SyntetiserArenaRequest arenaRequest) {
        List<String> registrerteIdenter = syntetiseringService.sendBrukereTilArenaForvalterConsumer(
                arenaRequest.getAntallNyeIdenter(),
                arenaRequest.getAvspillergruppeId(),
                arenaRequest.getMiljoe()
        ).stream().map(Arbeidsoker::getPersonident).collect(Collectors.toList());

        return ResponseEntity.ok().body(registrerteIdenter);
    }

    private ResponseEntity<List<String>> slettBrukere(String miljoe, List<String> identer) {

        List<String> slettedeIdenter = new ArrayList<>(syntetiseringService.slettBrukereIArenaForvalter(identer, miljoe));

        return ResponseEntity.ok(slettedeIdenter);
    }

}
