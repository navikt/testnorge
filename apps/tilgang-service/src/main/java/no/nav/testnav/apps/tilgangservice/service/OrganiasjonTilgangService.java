package no.nav.testnav.apps.tilgangservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import no.nav.testnav.apps.tilgangservice.client.altinn.v1.AltinnClient;
import no.nav.testnav.apps.tilgangservice.domain.Organisajon;

@Service
@RequiredArgsConstructor
public class OrganiasjonTilgangService {
    private final AltinnClient client;

    public Flux<Organisajon> getAll() {
        return client.getOrganiasjoner();
    }

    public Mono<Organisajon> create(String organiasjonsnummer, LocalDateTime gyldigTil) {
        return client.create(organiasjonsnummer, gyldigTil);
    }

    public Flux<Void> delete(String organiasjonsnummer) {
        return client.delete(organiasjonsnummer);
    }

}
