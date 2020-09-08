package no.nav.registre.testnorge.libs.core.provider;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("prod")
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
