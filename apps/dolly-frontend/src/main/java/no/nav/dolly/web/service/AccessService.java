package no.nav.dolly.web.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.web.consumers.PersonOrganisasjonTilgangConsumer;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class AccessService {
    private final PersonOrganisasjonTilgangConsumer personOrganisasjonTilgangConsumer;

    public Mono<Boolean> hasAccess(String organisasjonsnummer, ServerWebExchange serverWebExchange) {
        return personOrganisasjonTilgangConsumer.hasAccess(organisasjonsnummer, serverWebExchange);
    }
}
