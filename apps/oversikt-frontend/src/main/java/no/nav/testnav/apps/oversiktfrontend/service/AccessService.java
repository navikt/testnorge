package no.nav.testnav.apps.oversiktfrontend.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.oversiktfrontend.consumer.AltinnTilgangServiceConsumer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccessService {
    private final AltinnTilgangServiceConsumer altinnTilgangServiceConsumer;

    public Mono<Boolean> hasAccess(String organisasjonsnummer) {
        return altinnTilgangServiceConsumer.hasAccess(organisasjonsnummer);
    }
}
