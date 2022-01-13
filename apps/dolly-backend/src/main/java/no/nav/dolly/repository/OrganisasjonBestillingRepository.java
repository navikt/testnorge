package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrganisasjonBestillingRepository extends Repository<OrganisasjonBestilling, Long> {

    Optional<OrganisasjonBestilling> findById(Long id);

    @Modifying
    OrganisasjonBestilling save(OrganisasjonBestilling bestilling);

    @Modifying
    @Query(value = "delete from OrganisasjonBestilling b where b = :bestilling and not exists (select bp from OrganisasjonBestillingProgress bp where bp.bestilling = :bestilling)")
    int deleteBestillingWithNoChildren(@Param("bestilling") OrganisasjonBestilling bestilling);

    Optional<List<OrganisasjonBestilling>> findByBruker(Bruker bruker);
}
