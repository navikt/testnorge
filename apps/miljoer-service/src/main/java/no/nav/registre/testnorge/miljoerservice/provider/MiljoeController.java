package no.nav.registre.testnorge.miljoerservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.miljoerservice.service.MiljoerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/miljoer")
@RequiredArgsConstructor
public class MiljoeController {

    private final MiljoerService service;

    @GetMapping
    public ResponseEntity<List<String>> hentAktiveMiljoer() {
        return ResponseEntity.ok(service.getMiljoer());
    }
}
