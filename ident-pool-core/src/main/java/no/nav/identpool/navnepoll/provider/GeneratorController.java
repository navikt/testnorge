package no.nav.identpool.navnepoll.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.identpool.navnepoll.NavnepollService;
import no.nav.identpool.navnepoll.domain.Navn;

@RestController
@RequestMapping("/navnepoll")
public class GeneratorController {
    @Autowired
    private NavnepollService service;

    @GetMapping("/generer")
    public ResponseEntity<Navn> get() {
        return ResponseEntity.ok(service.finnTilfeldigNavn());
    }
}
