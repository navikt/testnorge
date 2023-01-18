package no.nav.testnav.apps.oversiktfrontend.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.oversiktfrontend.consumer.PersonOrganisasjonTilgangConsumer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccessService {
    private final PersonOrganisasjonTilgangConsumer personOrganisasjonTilgangConsumer;

    public Mono<Boolean> hasAccess(String organisasjonsnummer) {
        return personOrganisasjonTilgangConsumer.hasAccess(organisasjonsnummer);
    }
}
