package no.nav.testnav.libs.servletcore.provider;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile({"prod","dev"})
@RestController
@RequestMapping("/internal")
public class InternalController {

    @GetMapping("/isAlive")
    @Operation(hidden = true)
    public ResponseEntity<HttpStatus> isAlive() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/isReady")
    @Operation(hidden = true)
    public ResponseEntity<HttpStatus> isReady() {
        return ResponseEntity.ok().build();
    }

}