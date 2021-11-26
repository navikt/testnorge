package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.BestillingKontroll;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BestillingKontrollRepository extends Repository<BestillingKontroll, Long> {

    Optional<BestillingKontroll> save(BestillingKontroll bestillingKontroll);

    Optional<BestillingKontroll> findByBestillingId(Long bestillingId);

    @Modifying
    @Query("delete from BestillingKontroll bk where bk.bestillingId in (select b.id from Bestilling b where b.gruppe.id = :gruppeId)")
    int deleteByGruppeId(@Param("gruppeId") Long gruppeId);

    @Modifying
    @Query("delete from BestillingKontroll bk where bk.bestillingId = :bestillingId and bk.bestillingId "
            + "not in (select bp.bestilling.id from BestillingProgress bp where bp.bestilling.id = :bestillingId)")
    int deleteByBestillingWithNoChildren(@Param("bestillingId") Long bestillingId);
}