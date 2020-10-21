package no.nav.registre.testnorge.identservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.identservice.service.IdentServiceAppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/helloWorld")
public class IdentServiceController {
    private final IdentServiceAppService identServiceAppService;

    @GetMapping
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok().body("Hello World!");
    }
}