package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.BestillingKontroll;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface BestillingKontrollRepository extends ReactiveCrudRepository<BestillingKontroll, Long> {

    Mono<BestillingKontroll> findByBestillingId(Long bestillingId);

    @Modifying
    Mono<Integer> deleteByGruppeId(Long gruppeId);

    @Modifying
    @Query("""
            delete from bestilling_kontroll bk
                        where bk.bestilling_id = :bestillingId
                        and bk.bestilling_id not in (select bp.bestilling_id
                        from Bestilling_Progress bp where bp.bestilling_id = :bestillingId)
            """)
    Mono<Integer> deleteByBestillingWithNoChildren(@Param("bestillingId") Long bestillingId);
}