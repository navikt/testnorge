package no.nav.dolly.web.provider.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/session")
public class SessionController {

    /**
     * Ping endepunkt for aa holde sessionen aapen.
     */
    @GetMapping("/ping")
    public ResponseEntity<HttpStatus> ping() {
        return ResponseEntity.noContent().build();
    }
}
