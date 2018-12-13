package no.nav.dolly.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import no.nav.dolly.domain.jpa.Testident;

public interface IdentRepository extends CrudRepository<Testident, String> {

    Testident findByIdent(String ident);

    @Modifying
    @Query(value = "delete from Testident ti "
            + "where ti.ident in "
            + "(select ident from BestillingProgress bp "
            + "where bp.bestillingId = :bestillingId)")
    int deleteTestidentsByBestillingId(@Param("bestillingId") Long bestillingId);

    void deleteTestidentByIdent(String testident);
}
