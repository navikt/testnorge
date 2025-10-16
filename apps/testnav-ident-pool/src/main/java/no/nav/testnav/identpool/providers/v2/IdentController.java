package no.nav.testnav.identpool.providers.v2;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.identpool.providers.v1.support.RekvirerIdentRequest;
import no.nav.testnav.identpool.service.Identpool32Service;
import no.nav.testnav.identpool.service.PersonnummerValidatorService;
import no.nav.testnav.identpool.util.ValiderRequestUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static java.util.Objects.requireNonNull;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/ident")
public class IdentController {

    private final Identpool32Service identpool32Service;

    @Operation(description = "rekvirer nye test-identer for pid2032")
    @PostMapping("/rekvirer")
    public Mono<String> rekvirer(RekvirerIdentRequest request) {

        // validering her
        ValiderRequestUtil.validateDatesInRequest(request);

        return identpool32Service.generateIdent(request);
    }

    @GetMapping("/valider")
    public Mono<String> valider(String ident) {

        return Mono.just(requireNonNull(PersonnummerValidatorService.validerFoedselsnummer(ident)));
    }
}
