package no.nav.testnav.altinn3tilgangservice.database.repository;

import no.nav.testnav.altinn3tilgangservice.database.entity.OrganisasjonTilgang;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface OrganisasjonTilgangRepository extends ReactiveCrudRepository<OrganisasjonTilgang, Long> {

    Mono<Boolean> existsByOrganisasjonNummer(String orgnummer);

    Mono<OrganisasjonTilgang> findByOrganisasjonNummer(String orgnummer);

    Mono<OrganisasjonTilgang> save(OrganisasjonTilgang organisasjonTilgang);

    Mono<Integer> deleteByOrganisasjonNummer(String orgnummer);
}