package no.nav.testnav.apps.tilgangservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.tilgangservice.client.altinn.v1.AltinnClient;
import no.nav.testnav.apps.tilgangservice.domain.Access;

@Service
@RequiredArgsConstructor
public class PersonTilgangSerivce {

    private final AltinnClient client;

    public Flux<Access> getAccess() {
        return client.getAccess();
    }

    public Mono<Access> getAccess(String organiasjonsnummer) {
        return client.getAccess(organiasjonsnummer);
    }
}
