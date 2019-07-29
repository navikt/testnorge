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
    public ResponseEntity<Map<String, List<String>>> registerBrukereIArenaForvalter(@RequestBody SyntetiserArenaRequest syntetiserArenaRequest) {
        return registrerBrukereIArenaForvalter(syntetiserArenaRequest);
    }

    @PostMapping(value = "/slett")
    public ResponseEntity<Map<String, List<String>>> slettBrukereIArenaForvalter(@RequestParam String miljoe, @RequestBody List<String> identer) {
        return slettBrukere(miljoe, identer);
    }


    private ResponseEntity<Map<String, List<String>>> registrerBrukereIArenaForvalter(SyntetiserArenaRequest arenaRequest) {
        List<String> registrerteIdenter = syntetiseringService.sendBrukereTilArenaForvalterConsumer(
                arenaRequest.getAntallNyeIdenter(),
                arenaRequest.getAvspillergruppeId(),
                arenaRequest.getMiljoe()
        ).stream().map(Arbeidsoker::getPersonident).collect(Collectors.toList());

        Map<String, List<String>> responseBody = new HashMap<>();
        responseBody.put("registrerteIdenter", registrerteIdenter);

        return ResponseEntity.ok().body(responseBody);
    }

    private ResponseEntity<Map<String, List<String>>> slettBrukere(String miljoe, List<String> identer) {

        Map<String, List<String>> responseBody = new HashMap<>();
        List<String> alleIdenter = new ArrayList<>(identer);

        List<String> slettedeIdenter = new ArrayList<>(
                syntetiseringService.slettBrukereIArenaForvalter(identer, miljoe));

        responseBody.put("slettet", slettedeIdenter);

        alleIdenter.removeAll(slettedeIdenter);
        responseBody.put("ikkeSlettet", alleIdenter);

        return ResponseEntity.ok(responseBody);
    }

}
