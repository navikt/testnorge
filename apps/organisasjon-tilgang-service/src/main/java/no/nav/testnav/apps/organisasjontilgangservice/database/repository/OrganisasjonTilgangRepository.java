package no.nav.testnav.apps.organisasjontilgangservice.database.repository;

import no.nav.testnav.apps.organisasjontilgangservice.database.entity.OrganisasjonTilgang;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface OrganisasjonTilgangRepository extends ReactiveCrudRepository<OrganisasjonTilgang, Long> {

    Mono<OrganisasjonTilgang> getOrganisasjonTilgangByOrganisasjonNummer(String orgnummer);

    Mono<OrganisasjonTilgang> save(OrganisasjonTilgang organisasjonTilgang);

    Mono<Void> deleteByOrganisasjonNummer(String orgnisasjonsnummer);
}
