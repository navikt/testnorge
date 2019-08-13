package no.nav.registre.arena.core.provider.rs;


import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import no.nav.registre.arena.core.service.IdentService;
import no.nav.registre.arena.domain.Arbeidsoeker;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("api/v1/ident")
@RequiredArgsConstructor
public class IdentController {

    private final IdentService identService;

    @DeleteMapping("/slett")
    @ApiOperation(value = "Slett identer fra Arena", notes = "Sletter oppgitte identer fra Arena. \nResponse: liste over alle innsendte identer som ble slettet.")
    public ResponseEntity<List<String>> slettBrukereIArenaForvalter(@RequestParam String miljoe, @RequestBody List<String> identer) {
        return slettBrukere(miljoe, identer);
    }

    @GetMapping("/hent")
    @ApiOperation(value = "Hent brukere", notes = "Henter alle brukere, filtrert på de gitte parameterene, som er registrert i Arena.")
    public ResponseEntity<List<Arbeidsoeker>> hentBrukereFraArenaForvalter(@RequestParam(required = false) String eier,
                                                                           @RequestParam(required = false) String miljoe,
                                                                           @RequestBody(required = false) List<String> personidenter) {
        return ResponseEntity.ok(identService.hentArbeidsoekere(eier, miljoe, personidenter));
    }

    private ResponseEntity<List<String>> slettBrukere(String miljoe, List<String> identer) {

        List<String> slettedeIdenter = new ArrayList<>(identService.slettBrukereIArenaForvalter(identer, miljoe));
        return ResponseEntity.ok(slettedeIdenter);
    }
}
