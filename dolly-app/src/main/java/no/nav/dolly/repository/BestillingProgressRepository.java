package no.nav.dolly.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import no.nav.dolly.domain.jpa.BestillingProgress;

public interface BestillingProgressRepository extends CrudRepository<BestillingProgress, Long> {

    List<BestillingProgress> findBestillingProgressByBestillingIdOrderByBestillingId(Long bestillingId);

    @Modifying
    void deleteByBestillingId(Long bestillingId);

    @Modifying
    @Query("delete from BestillingProgress bp where bp.bestillingId in (select b.id from Bestilling b where b.gruppe.id = :gruppeId)")
    int deleteByGruppeId(@Param("gruppeId") Long gruppeId);
}
