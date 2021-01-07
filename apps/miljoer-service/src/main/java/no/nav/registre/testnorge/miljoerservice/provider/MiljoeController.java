package no.nav.registre.testnorge.miljoerservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.miljoerservice.consumer.TpsfConsumer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/miljoer")
@RequiredArgsConstructor
public class MiljoeController {

    private final TpsfConsumer tpsfConsumer;

    @GetMapping
    @Operation(description = "Tjeneste for å sjekke hvilke miljøer i test og preprod som er tilgjengelige nå")
    public ResponseEntity<List<String>> hentAktiveMiljoer() {
        return ResponseEntity.ok(tpsfConsumer.getAktiveMiljoer().getEnvironments());
    }
}
