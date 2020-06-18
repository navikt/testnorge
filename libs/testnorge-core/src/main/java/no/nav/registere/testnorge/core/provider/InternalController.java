package no.nav.registere.testnorge.core.provider;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class InternalController {

    @GetMapping("/isAlive")
    public ResponseEntity<HttpStatus> isAlive() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/isReady")
    public ResponseEntity<HttpStatus> isReady() {
        return ResponseEntity.ok().build();
    }

}
