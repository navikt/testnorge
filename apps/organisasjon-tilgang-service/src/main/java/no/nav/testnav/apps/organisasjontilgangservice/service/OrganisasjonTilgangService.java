package no.nav.testnav.apps.organisasjontilgangservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.organisasjontilgangservice.consumer.altinn.v1.AltinnConsumer;
import no.nav.testnav.apps.organisasjontilgangservice.domain.Organisasjon;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrganisasjonTilgangService {
    private final AltinnConsumer client;

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
