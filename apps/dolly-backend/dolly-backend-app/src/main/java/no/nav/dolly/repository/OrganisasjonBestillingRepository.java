package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrganisasjonBestillingRepository extends Repository<OrganisasjonBestilling, Long> {

    Optional<OrganisasjonBestilling> findById(Long id);

    @Query("from OrganisasjonBestilling ob where ob.bruker.brukerId = :brukerId")
    Optional<List<OrganisasjonBestilling>> findbyBrukerId(String brukerId);

    @Modifying
    OrganisasjonBestilling save(OrganisasjonBestilling bestilling);

    @Modifying
    @Query(value = "delete from OrganisasjonBestilling b where b.id = :bestillingId and not exists (select bp from OrganisasjonBestillingProgress bp where bp.bestillingId = :bestillingId)")
    int deleteBestillingWithNoChildren(@Param("bestillingId") Long bestillingId);
}
