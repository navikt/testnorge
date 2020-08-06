package no.nav.registre.orkestratoren.provider.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.orkestratoren.service.OrkestreingsService;

@RestController
@RequestMapping("/api/v1/orkestrering")
@RequiredArgsConstructor
public class OrkestreringsController {

    private final OrkestreingsService service;

    @PostMapping("/sykemeldinger")
    public ResponseEntity<HttpStatus> triggerSykemeldinger() {
        service.orkisterSykemeldinger();
        return ResponseEntity.ok().build();
    }
}
