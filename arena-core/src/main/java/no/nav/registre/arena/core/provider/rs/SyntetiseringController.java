package no.nav.registre.arena.core.provider.rs;


import no.nav.registre.arena.core.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.arena.core.service.SyntetiseringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringController {
    @Autowired
    SyntetiseringService syntetiseringService;

    @PostMapping(value = "/generer")
    public ResponseEntity registerBrukereIArenaForvalter(@RequestBody SyntetiserArenaRequest syntetiserArenaRequest) {
        return syntetiseringService.registrerBrukereIArenaForvalter(syntetiserArenaRequest);
    }
}
