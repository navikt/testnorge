package no.nav.registre.testnorge.miljoerservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.miljoerservice.response.MiljoerResponse;
import no.nav.registre.testnorge.miljoerservice.service.MiljoerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/miljoer")
@RequiredArgsConstructor
public class MiljoeController {

    private final MiljoerService service;

    @GetMapping
    @Operation(description = "Hent liste med aktive miljøer fra TPSF")
    public ResponseEntity<MiljoerResponse> hentAktiveMiljoer() {
        return ResponseEntity.ok(service.getAktiveMiljoer());
    }
}
