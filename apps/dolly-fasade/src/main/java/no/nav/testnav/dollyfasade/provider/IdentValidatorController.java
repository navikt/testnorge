package no.nav.testnav.dollyfasade.provider;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollyfasade.consumers.IdentpoolConsumer;
import no.nav.testnav.libs.dto.identpool.v2.ValideringFlereRequestDTO;
import no.nav.testnav.libs.dto.identpool.v2.ValideringResponseDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/ident/validerflere")
public class IdentValidatorController {

    private final IdentpoolConsumer identpoolConsumer;

    @PostMapping
    @Operation(description = "Validering for nye og gamle test-identer, validerer flere identer separert med mellomrom, komma eller semikolon")
    public Flux<ValideringResponseDTO> validerflere(@Valid @RequestBody ValideringFlereRequestDTO request) {

        return identpoolConsumer.validerflere(request);
    }
}