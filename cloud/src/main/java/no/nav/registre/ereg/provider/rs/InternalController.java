package no.nav.registre.ereg.provider.rs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class InternalController {

    @GetMapping("/isAlive")
    public ResponseEntity isAlive() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/isReady")
    public ResponseEntity isReady() {
        return ResponseEntity.ok().build();
    }

}
