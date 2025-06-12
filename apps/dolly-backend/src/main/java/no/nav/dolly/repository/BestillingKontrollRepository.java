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
    Mono<Integer> deleteByGruppeId(@Param("gruppeId") Long gruppeId);

    @Modifying
    @Query("delete from BestillingKontroll bk where bk.bestillingId = :bestillingId and bk.bestillingId "
            + "not in (select bp.bestilling.id from BestillingProgress bp where bp.bestilling.id = :bestillingId)")
    int deleteByBestillingWithNoChildren(@Param("bestillingId") Long bestillingId);
}