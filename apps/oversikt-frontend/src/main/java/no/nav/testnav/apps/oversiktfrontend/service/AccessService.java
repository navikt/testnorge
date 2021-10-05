package no.nav.testnav.apps.oversiktfrontend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.oversiktfrontend.consumer.PersonOrganisasjonTilgangConsumer;

@Service
@RequiredArgsConstructor
public class AccessService {
    private final PersonOrganisasjonTilgangConsumer personOrganisasjonTilgangConsumer;

    public Mono<Boolean> hasAccess(String organisasjonsnummer, ServerWebExchange serverWebExchange) {
        return personOrganisasjonTilgangConsumer.hasAccess(organisasjonsnummer, serverWebExchange);
    }
}
