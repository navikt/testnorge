package no.nav.testnav.apps.oversiktfrontend.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/organisasjon")
public class OrganisasjonController {

    @PutMapping("/{organisasjonsnummer}")
    public Mono<Void> setOrganisasjonsnummer(@PathVariable String organisasjonsnummer, ServerWebExchange exchange) {
        return exchange.getSession().map(session -> {
            session.getAttributes().put("organisasjonsnummer", organisasjonsnummer);
            return session;
        }).then();
    }
}
