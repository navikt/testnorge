package no.nav.testnav.apps.organisasjontilgangservice.database.repository;

import no.nav.testnav.apps.organisasjontilgangservice.database.entity.OrganisasjonTilgang;
import org.springframework.data.repository.Repository;
import reactor.core.publisher.Mono;

public interface OrganisasjonTilgangRepository extends Repository<OrganisasjonTilgang, Long> {

    Mono<OrganisasjonTilgang> getOrganisasjonTilgangByOrganisasjonNummer(String orgnummer);

    Mono<OrganisasjonTilgang> save(OrganisasjonTilgang organisasjonTilgang);
}
