package no.nav.testnav.apps.tilgangservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import no.nav.testnav.apps.tilgangservice.client.altinn.v1.AltinnClient;
import no.nav.testnav.apps.tilgangservice.domain.Organisasjon;

@Service
@RequiredArgsConstructor
public class OrganisasjonTilgangService {
    private final AltinnClient client;

    public Flux<Organisasjon> getAll() {
        return client.getOrganisasjoner();
    }

    public Mono<Organisasjon> create(String organisasjonsnummer, LocalDateTime gyldigTil) {
        return client.create(organisasjonsnummer, gyldigTil);
    }

    public Flux<Void> delete(String organisasjonsnummer) {
        return client.delete(organisasjonsnummer);
    }

}
