package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface OrganisasjonBestillingProgressRepository extends Repository<OrganisasjonBestillingProgress, Long> {

    @Modifying
    Optional<OrganisasjonBestillingProgress> save(OrganisasjonBestillingProgress bestillingProgress);

    Optional<List<OrganisasjonBestillingProgress>> findByBestillingId(Long bestillingId);

    @Query("from OrganisasjonBestillingProgress obp where obp.bestillingId in (select ob.id from OrganisasjonBestilling ob where ob.bruker.brukerId = :brukerId)")
    Optional<List<OrganisasjonBestillingProgress>> findbyBrukerId(@Param("brukerId") String brukerId);

    @Transactional
    @Modifying
    int deleteByBestillingId(Long bestillingId);

    @Transactional
    @Modifying
    int deleteByOrganisasjonsnummer(String orgnummer);
}
