package no.nav.registre.arena.core.provider.rs;

import no.nav.registre.arena.core.provider.rs.requests.SyntetiserArenaForvalterRequest;
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

    @PostMapping("/generer")
    public ResponseEntity genererAntallMeldingerOgMiljo(@RequestBody SyntetiserArenaForvalterRequest syntetiserArenaForvalterRequest) {
        return syntetiseringService.finnSyntetiserteMeldinger(syntetiserArenaForvalterRequest);
    }
}
