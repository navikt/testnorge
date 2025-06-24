package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrganisasjonBestillingRepository extends ReactiveCrudRepository<OrganisasjonBestilling, Long> {

    @Modifying
    @Query("""
            delete from organisasjon_bestilling b
            where b = :bestilling and not exists
            (select bp from organisasjon_bestilling_progress bp where bp.bestilling_id = :bestillingId)
            """)
    Mono<Void> deleteBestillingWithNoChildren(@Param("bestillingId") Long bestillingId);

    Flux<OrganisasjonBestilling> findByBrukerId(Long brukerId);

    @Modifying
    @Query("""
                    update organisasjon_bestilling ob
                    set ferdig = true
                    where ob.ferdig = false
            """)
    Mono<Void> stopAllUnfinished();
}