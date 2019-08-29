package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.BestillingProgress;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BestillingProgressRepository extends Repository<BestillingProgress, Long> {

    Optional<BestillingProgress> save(BestillingProgress bestillingProgress);

    List<BestillingProgress> findBestillingProgressByBestillingIdOrderByBestillingId(Long bestillingId);

    void deleteByBestillingId(Long bestillingId);

    @Modifying
    @Query("delete from BestillingProgress bp where bp.bestillingId in (select b.id from Bestilling b where b.gruppe.id = :gruppeId)")
    int deleteByGruppeId(@Param("gruppeId") Long gruppeId);
}
