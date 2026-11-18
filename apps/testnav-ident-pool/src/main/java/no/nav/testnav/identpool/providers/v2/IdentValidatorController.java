package no.nav.testnav.identpool.providers.v2;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.testnav.identpool.dto.ValideringFlereRequestDTO;
import no.nav.testnav.identpool.dto.ValideringResponseDTO;
import no.nav.testnav.identpool.service.PersonnummerValidatorService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/v2/ident/validerFlere", "/api/v2/ident/validerflere"})
public class IdentValidatorController {

    private final PersonnummerValidatorService personnummerValidatorService;

    @PostMapping
    @Operation(description = "Validering for nye og gamle test-identer, validerer flere identer separert med mellomrom, komma eller semikolon")
    public Flux<ValideringResponseDTO> validerflere(@Valid @RequestBody ValideringFlereRequestDTO request) {

        val identer = request.identer().split("[\\s,;]");
        return Flux.fromArray(identer)
                .filter(StringUtils::isNotBlank)
                .buffer(80)
                .concatMap(personnummerValidatorService::validerFoedselsnummer);
    }
}
