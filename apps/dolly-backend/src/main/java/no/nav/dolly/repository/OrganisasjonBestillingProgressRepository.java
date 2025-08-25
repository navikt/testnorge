package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OrganisasjonBestillingProgressRepository extends ReactiveCrudRepository<OrganisasjonBestillingProgress, Long> {

    Flux<OrganisasjonBestillingProgress> findByBestillingId(Long bestillingId);

    @Modifying
    Mono<Void> deleteByBestillingId(Long bestillingId);

    @Modifying
    Flux<Void> deleteByOrganisasjonsnummer(String orgnummer);

    Flux<OrganisasjonBestillingProgress> findByOrganisasjonsnummer(String orgnummer);
}
