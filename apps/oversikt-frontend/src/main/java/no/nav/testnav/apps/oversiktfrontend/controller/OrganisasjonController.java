package no.nav.testnav.apps.oversiktfrontend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.oversiktfrontend.consumer.PersonOrganisasjonTilgangConsumer;
import no.nav.testnav.apps.oversiktfrontend.consumer.dto.OrganisasjonDTO;
import no.nav.testnav.apps.oversiktfrontend.service.AccessService;
import no.nav.testnav.libs.reactivesessionsecurity.config.TokenXConstants;

@RestController
@RequestMapping("/api/v1/organisasjon")
@RequiredArgsConstructor
public class OrganisasjonController {

    private final AccessService accessService;
    private final PersonOrganisasjonTilgangConsumer personOrganisasjonTilgangConsumer;

    @GetMapping
    public Flux<OrganisasjonDTO> getOrganisasjoner(ServerWebExchange exchange) {
        return personOrganisasjonTilgangConsumer
                .getOrganisasjoner(exchange);
    }

    @PutMapping("/{organisasjonsnummer}")
    public Mono<ResponseEntity<?>> setOrganisasjonsnummer(@PathVariable String organisasjonsnummer, ServerWebExchange exchange) {
        return accessService
                .hasAccess(organisasjonsnummer, exchange)
                .flatMap(hasAccess -> {
                    if (!hasAccess) {
                        return Mono.just(ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .build());
                    }
                    return exchange
                            .getSession()
                            .doOnSuccess(session -> session.getAttributes().put(TokenXConstants.TOKENX_PERSON_REPRESENTING_KEY, organisasjonsnummer))
                            .then()
                            .map(value -> ResponseEntity.ok().build());
                });
    }
}