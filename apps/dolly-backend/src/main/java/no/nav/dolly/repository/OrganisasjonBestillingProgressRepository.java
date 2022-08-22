package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface OrganisasjonBestillingProgressRepository extends Repository<OrganisasjonBestillingProgress, Long> {

    @Modifying
    Optional<OrganisasjonBestillingProgress> save(OrganisasjonBestillingProgress bestillingProgress);

    Optional<List<OrganisasjonBestillingProgress>> findByBestillingId(Long bestillingId);

    @Modifying
    int deleteByBestillingId(Long bestillingId);

    @Modifying
    int deleteByOrganisasjonsnummer(String orgnummer);

    Optional<List<OrganisasjonBestillingProgress>> findByOrganisasjonsnummer(String orgnummer);
}
