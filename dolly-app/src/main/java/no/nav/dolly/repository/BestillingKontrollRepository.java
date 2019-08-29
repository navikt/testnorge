package no.nav.dolly.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import no.nav.dolly.domain.jpa.BestillingKontroll;

public interface BestillingKontrollRepository extends CrudRepository<BestillingKontroll, Long> {

    Optional<BestillingKontroll> findByBestillingIdOrderByBestillingId(Long bestillingId);

    @Modifying
    @Query("delete from BestillingKontroll bk where bk.bestillingId in (select b.id from Bestilling b where b.gruppe.id = :gruppeId)")
    int deleteByGruppeId(@Param("gruppeId") Long gruppeId);
}