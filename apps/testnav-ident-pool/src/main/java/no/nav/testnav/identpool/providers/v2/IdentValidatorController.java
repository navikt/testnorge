package no.nav.testnav.identpool.providers.v2;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.testnav.libs.dto.identpool.v2.ValideringFlereRequestDTO;
import no.nav.testnav.identpool.service.PersonnummerValidatorService;
import no.nav.testnav.libs.dto.identpool.v2.ValideringResponseDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/ident/validerflere")
public class IdentValidatorController {

    private final PersonnummerValidatorService personnummerValidatorService;

    @PostMapping
    @Operation(description = "Validering for nye og gamle test-identer, validerer flere identer separert med mellomrom, komma eller semikolon")
    public Flux<ValideringResponseDTO> validerflere(@Valid @RequestBody ValideringFlereRequestDTO request) {

        val identer = request.identer().split("[\\s,;]");

        return Flux.fromArray(identer)
                .filter(StringUtils::isNotBlank)
                .buffer(80)
                .concatMap(personnummerValidatorService::validerFoedselsnummer)
                .collectList()
                .flatMap(resultat -> getNavIdent()
                        .zipWith(Mono.just(resultat)))
                .flatMapMany(tuple -> {
                    log.info("Identvalidering utført av: {}, totalt antall: {}, herav i prod: {}", tuple.getT1(),
                            tuple.getT2().size(),
                            tuple.getT2().stream()
                                    .filter(response -> isTrue(response.erIProd()))
                                    .count());
                    return Flux.fromIterable(tuple.getT2());
                });
    }

    private Mono<String> getNavIdent() {

        return ReactiveSecurityContextHolder.getContext()
                .mapNotNull(SecurityContext::getAuthentication)
                .filter(JwtAuthenticationToken.class::isInstance)
                .map(JwtAuthenticationToken.class::cast)
                .map(JwtAuthenticationToken::getTokenAttributes)
                .mapNotNull(map -> (String) map.get("NAVident"))
                .switchIfEmpty(Mono.just("Ukjent"));
    }
}