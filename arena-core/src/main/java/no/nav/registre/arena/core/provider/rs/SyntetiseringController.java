package no.nav.registre.arena.core.provider.rs;

import no.nav.registre.arena.core.provider.rs.requests.SlettArenaRequest;
import no.nav.registre.arena.core.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.arena.core.service.SyntetiseringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        if (arenaRequest.getAntallNyeIdenter() != null)
            return ResponseEntity.ok(syntetiseringService.sendBrukereTilArenaForvalterConsumer(arenaRequest).size());


        int antallBrukereAaOpprette = syntetiseringService.getAntallBrukereForAaFylleArenaForvalteren(arenaRequest);

        if (antallBrukereAaOpprette > 0) {
            arenaRequest.setAntallNyeIdenter(antallBrukereAaOpprette);
            return ResponseEntity.ok(syntetiseringService.sendBrukereTilArenaForvalterConsumer(arenaRequest).size());
        }

        return ResponseEntity.ok(0);
    }

    private ResponseEntity<Map<String, Integer>> slettBrukere(SlettArenaRequest slettArenaRequest) {

        Map<String, Integer> responseBody = new HashMap<>();

        List<String> slettedeIdenter = syntetiseringService.slettBrukereIArenaForvalter(slettArenaRequest);
        responseBody.put("Antall slettede identer", slettedeIdenter.size());

        List<String> alleIdenter = slettArenaRequest.getIdenter();
        alleIdenter.removeAll(slettedeIdenter);
        responseBody.put("Identer som ikke kunne slettes", alleIdenter.size());

        return ResponseEntity.ok(responseBody);
    }

}
