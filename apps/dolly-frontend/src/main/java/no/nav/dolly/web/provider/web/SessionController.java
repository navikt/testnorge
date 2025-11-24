package no.nav.dolly.web.provider.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.consumers.Altinn3PersonOrganisasjonTilgangConsumer;
import no.nav.dolly.web.service.BrukerService;
import no.nav.testnav.libs.securitycore.config.UserSessionConstant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/session")
@Slf4j
@RequiredArgsConstructor
public class SessionController {

    private final BrukerService brukerService;

    private final Altinn3PersonOrganisasjonTilgangConsumer altinn3PersonOrganisasjonTilgangConsumer;

    /**
     * Ping endepunkt for aa holde sessionen aapen.
     */
    @GetMapping("/ping")
    public ResponseEntity<HttpStatus> ping() {
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user")
    public Mono<ResponseEntity<?>> delete(ServerWebExchange exchange) {
        return exchange
                .getSession()
                .doOnSuccess(session -> session.getAttributes().remove(UserSessionConstant.SESSION_USER_ID_KEY))
                .map(value -> ResponseEntity.noContent().build());
    }

    @PutMapping("/user")
    public Mono<ResponseEntity<?>> addUserToSession(@RequestParam String organisasjonsnummer, ServerWebExchange exchange) {

        return altinn3PersonOrganisasjonTilgangConsumer
                .hasAccess(organisasjonsnummer, exchange)
                .doOnError(e -> log.error("Feil ved sjekk av tilgang til org {}", organisasjonsnummer, e))
                .flatMap(hasAccess -> {
                    if (Boolean.FALSE.equals(hasAccess)) {
                        log.error("Bruker mangler tilgang til org {}", organisasjonsnummer);
                        return Mono.just(ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .build());
                    }
                    return brukerService.getId(organisasjonsnummer, exchange).flatMap(id -> exchange
                            .getSession()
                            .doOnError(e -> log.error("Feil ved lagring av bruker i session", e))
                            .doOnSuccess(session -> session.getAttributes().put(UserSessionConstant.SESSION_USER_ID_KEY, id))
                            .map(value -> ResponseEntity.ok().build())
                    ).switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
                });
    }

}