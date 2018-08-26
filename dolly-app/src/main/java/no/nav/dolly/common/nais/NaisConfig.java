package no.nav.dolly.common.nais;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class NaisConfig {

    @GetMapping("/isReady")
    public ResponseEntity isReady() {
        return ResponseEntity.ok("Ready");
    }

    @GetMapping("/isAlive")
    public ResponseEntity isAlive() {
        return ResponseEntity.ok("Alive");
    }
}
