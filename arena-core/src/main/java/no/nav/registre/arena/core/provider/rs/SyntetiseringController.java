package no.nav.registre.arena.core.provider.rs;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import no.nav.registre.arena.domain.Arbeidsoeker;
import no.nav.registre.arena.core.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.arena.core.service.SyntetiseringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class SyntetiseringController {

    private final SyntetiseringService syntetiseringService;

    @PostMapping(value = "/generer")
    @ApiOperation(value = "Legg til identer i Arena", notes = "Legger til oppgitt antall identer i Arena. Dersom ingen antall identer blir oppgitt fyller den opp slik at 20% tilgjengelige gyldige identer ligger i Arena. \nResponse: liste av opprettede identer.")
    public ResponseEntity<List<String>> registerBrukereIArenaForvalter(@RequestParam(required = false) String personident,
                                                                       @RequestBody(required = false) SyntetiserArenaRequest syntetiserArenaRequest) {
        if ("".equals(personident) || personident == null) {
            return registrerBrukereIArenaForvalter(syntetiserArenaRequest);
        }

        return registrerBrukerIArenaForvalter(personident, syntetiserArenaRequest);
    }

    @DeleteMapping(value = "/slett")
    @ApiOperation(value = "Slett identer fra Arena", notes = "Sletter oppgitte identer fra Arena. \nResponse: liste over alle innsendte identer som ble slettet.")
    public ResponseEntity<List<String>> slettBrukereIArenaForvalter(@RequestParam String miljoe, @RequestBody List<String> identer) {
        return slettBrukere(miljoe, identer);
    }

    @GetMapping(value = "/hent")
    @ApiOperation(value = "Hent brukere", notes = "Henter alle brukere som er registrert i Arena.")
    public ResponseEntity<List<Arbeidsoeker>> hentBrukereFraArenaForvalter(@RequestParam(required = false) String eier,
                                                                           @RequestParam(required = false) String miljoe,
                                                                           @RequestParam(required = false) String personident) {
        return ResponseEntity.ok(syntetiseringService.hentArbeidsoekere(eier, miljoe, personident));
    }

    private ResponseEntity<List<String>> registrerBrukereIArenaForvalter(SyntetiserArenaRequest arenaRequest) {
        List<String> registrerteIdenter = syntetiseringService.opprettArbeidsoekere(
                arenaRequest.getAntallNyeIdenter(),
                arenaRequest.getAvspillergruppeId(),
                arenaRequest.getMiljoe()
        ).stream().map(Arbeidsoeker::getPersonident).collect(Collectors.toList());

        return ResponseEntity.ok().body(registrerteIdenter);
    }

    private ResponseEntity<List<String>> registrerBrukerIArenaForvalter(String personident, SyntetiserArenaRequest arenaRequest) {
        List<String> registrerteIdenter = syntetiseringService.opprettArbeidssoeker(
                personident,
                arenaRequest.getAvspillergruppeId(),
                arenaRequest.getMiljoe()
        ).stream().map(Arbeidsoeker::getPersonident).collect(Collectors.toList());

        return ResponseEntity.ok().body(registrerteIdenter);
    }

    private ResponseEntity<List<String>> slettBrukere(String miljoe, List<String> identer) {

        List<String> slettedeIdenter = new ArrayList<>(syntetiseringService.slettBrukereIArenaForvalter(identer, miljoe));
        return ResponseEntity.ok(slettedeIdenter);
    }

}
