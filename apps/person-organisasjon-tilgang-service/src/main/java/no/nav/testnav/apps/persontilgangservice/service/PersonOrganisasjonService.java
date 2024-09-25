package no.nav.testnav.apps.persontilgangservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.persontilgangservice.client.maskinporten.v1.MaskinportenClient;
import no.nav.testnav.apps.persontilgangservice.domain.AccessToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.persontilgangservice.client.altinn.v1.AltinnClient;
import no.nav.testnav.apps.persontilgangservice.domain.Access;

@Service
@RequiredArgsConstructor
public class PersonOrganisasjonService {

    private final AltinnClient client;
    private final MaskinportenClient maskinportenClient;

    public Flux<Access> getAccess() {
        return client.getAccess();
    }
    
    public Mono<AccessToken> getAccessToken() {
        return maskinportenClient.getAccessToken();
    }

    public Mono<Access> getAccess(String organiasjonsnummer) {
        return client.getAccess(organiasjonsnummer);
    }
}
