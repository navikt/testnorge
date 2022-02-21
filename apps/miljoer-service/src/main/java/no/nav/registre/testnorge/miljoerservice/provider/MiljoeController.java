package no.nav.registre.testnorge.miljoerservice.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.miljoerservice.service.MiljoerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/miljoer")
@RequiredArgsConstructor
public class MiljoeController {

    private final MiljoerService miljoerService;

    @GetMapping
    @Operation(description = "Tjeneste for å sjekke hvilke miljøer i test og preprod som er tilgjengelige nå")
    public List<String> hentAktiveMiljoer() {

        return miljoerService.getMiljoer();
    }
}
