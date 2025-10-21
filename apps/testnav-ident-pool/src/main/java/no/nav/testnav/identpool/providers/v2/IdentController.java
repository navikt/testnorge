package no.nav.testnav.identpool.providers.v2;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.identpool.dto.IdentpoolResponseDTO;
import no.nav.testnav.identpool.dto.ValideringResponseDTO;
import no.nav.testnav.identpool.providers.v1.support.RekvirerIdentRequest;
import no.nav.testnav.identpool.repository.PersonidentifikatorRepository;
import no.nav.testnav.identpool.service.Identpool32Service;
import no.nav.testnav.identpool.service.PersonnummerValidatorService;
import no.nav.testnav.identpool.util.ValiderRequestUtil;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/ident")
public class IdentController {

    private final Identpool32Service identpool32Service;
    private final PersonidentifikatorRepository personidentifikatorRepository;
    private final PersonnummerValidatorService personnummerValidatorService;

    @Operation(description = "Rekvirer nye test-identer for pid2032")
    @PostMapping("/rekvirer")
    public Mono<IdentpoolResponseDTO> rekvirer(@RequestBody RekvirerIdentRequest request) {

        // validering her
        ValiderRequestUtil.validateDatesInRequest(request);

        return identpool32Service.generateIdent(request);
    }

    @Operation(description = "Validering for nye og gamle test-identer")
    @GetMapping("/valider/{ident}")
    public Mono<ValideringResponseDTO> valider(@PathVariable String ident) {

        return personnummerValidatorService.validerFoedselsnummer(ident);
    }

    @Operation(description = "Frigjoer pid2032 test-ident")
    @DeleteMapping("/frigjoer/{ident}")
    public Mono<Void> frigjoer(@PathVariable String ident) {

        return personidentifikatorRepository.releaseByPersonidentifikator(ident);
    }
}
