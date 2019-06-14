package no.nav.registre.arena.provider.rs;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.arena.provider.rs.requests.SyntetiseringRequest;
import no.nav.registre.arena.service.SyntetiseringService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringController {

    @Autowired
    SyntetiseringService syntetiseringService;

    @LogExceptions
    @PostMapping("/generer")
    public List<ResponseEntity> genererAntallMeldingerOgMiljo(@RequestBody SyntetiseringRequest syntetiseringRequest) {
        return syntetiseringService.
    }
}
