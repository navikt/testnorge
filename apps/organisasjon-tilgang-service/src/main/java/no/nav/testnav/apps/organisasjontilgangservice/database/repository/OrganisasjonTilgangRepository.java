package no.nav.testnav.apps.organisasjontilgangservice.database.repository;

import no.nav.testnav.apps.organisasjontilgangservice.database.jpa.OrganisasjonTilgang;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface OrganisasjonTilgangRepository extends Repository<OrganisasjonTilgang, Long> {

    Mono<OrganisasjonTilgang> getOrganisasjonTilgangByOrganisasjonNummer(String orgnummer);

    Mono<OrganisasjonTilgang> save(OrganisasjonTilgang organisasjonTilgang);
}
