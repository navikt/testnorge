package no.nav.testnav.apps.oversiktfrontend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.oversiktfrontend.service.AccessService;
import no.nav.testnav.apps.oversiktfrontend.service.BrukerService;
import no.nav.testnav.libs.securitycore.UserSessionConstant;

@RestController
@RequestMapping("/api/v1/session/user")
@RequiredArgsConstructor
public class SessionController {
    private final AccessService accessService;
    private final BrukerService brukerService;

    @DeleteMapping
    public Mono<ResponseEntity<?>> delete(ServerWebExchange exchange) {
        return exchange
                .getSession()
                .doOnSuccess(session -> session.getAttributes().remove(UserSessionConstant.SESSION_USER_ID_KEY))
                .map(value -> ResponseEntity.noContent().build());
    }

    @PutMapping
    public Mono<ResponseEntity<?>> addUserToSession(@RequestParam String organisasjonsnummer, ServerWebExchange exchange) {
        return accessService
                .hasAccess(organisasjonsnummer, exchange)
                .flatMap(hasAccess -> {
                    if (!hasAccess) {
                        return Mono.just(ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .build());
                    }
                    return brukerService.getId(organisasjonsnummer, exchange).flatMap(id -> exchange
                            .getSession()
                            .doOnSuccess(session -> session.getAttributes().put(UserSessionConstant.SESSION_USER_ID_KEY, id))
                            .map(value -> ResponseEntity.ok().build())
                    ).switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
                });
    }
}