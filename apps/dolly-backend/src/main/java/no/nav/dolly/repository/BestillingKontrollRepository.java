package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.BestillingKontroll;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface BestillingKontrollRepository extends ReactiveCrudRepository<BestillingKontroll, Long> {

    Mono<BestillingKontroll> findByBestillingId(Long bestillingId);

    @Modifying
    @Query("delete from bestilling_kontroll bk where bk.bestilling_id in " +
            "(select b.id from Bestilling b where b.gruppe_id = :gruppeId)")
    Mono<Void> deleteByGruppeId(Long gruppeId);

    @Modifying
    @Query("""
            delete from bestilling_kontroll bk
            where bk.bestilling_id = :bestillingId
            and bk.bestilling_id not in (select bp.bestilling_id
                from Bestilling_Progress bp where bp.bestilling_id = :bestillingId)
            """)
    Mono<Void> deleteByBestillingWithNoChildren(@Param("bestillingId") Long bestillingId);
}