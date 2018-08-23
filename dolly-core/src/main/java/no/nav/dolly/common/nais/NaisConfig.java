package no.nav.dolly.common.nais;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NaisConfig {

    @GetMapping("/internal/isReady")
    public ResponseEntity isReady() {
        return ResponseEntity.ok("Ready");
    }

    @GetMapping("/internal/isAlive")
    public ResponseEntity isAlive() {
        return ResponseEntity.ok("Alive");
    }
}
