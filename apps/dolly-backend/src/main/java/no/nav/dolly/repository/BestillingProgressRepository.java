package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.BestillingProgress;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BestillingProgressRepository extends CrudRepository<BestillingProgress, Long> {

    Optional<List<BestillingProgress>> findByBestillingId(Long bestillingId);

    @Modifying
    @Query("delete from BestillingProgress bp where bp.bestilling.id in (select b.id from Bestilling b where b.gruppe.id = :gruppeId)")
    int deleteByGruppeId(@Param("gruppeId") Long gruppeId);

    @Modifying
    int deleteByIdent(String ident);

    @Modifying
    int deleteByBestilling_Id(Long bestillingId);

    List<BestillingProgress> findByIdent(String ident);

    List<BestillingProgress> findByBestilling_Id(Long bestillingId);

    @Modifying
    @Query(value = "update BestillingProgress bp set bp.ident = :newIdent where bp.ident = :oldIdent")
    void swapIdent(@Param(value = "oldIdent") String oldIdent, @Param(value = "newIdent") String newIdent);

    @Query(value = "select * from bestilling_progress where id = :id for update", nativeQuery = true)
    Optional<BestillingProgress> findByIdAndLock(@Param("id") Long id);
}
