package no.nav.common.nais;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NaisConfig {

    @GetMapping("/internal/ready")
    public ResponseEntity isReady() {
        return ResponseEntity.ok("Ready");
    }

    @GetMapping("/internal/alive")
    public ResponseEntity isAlive() {
        return ResponseEntity.ok("Alive");
    }
}
