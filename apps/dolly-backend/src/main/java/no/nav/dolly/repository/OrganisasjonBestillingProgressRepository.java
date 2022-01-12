package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface OrganisasjonBestillingProgressRepository extends Repository<OrganisasjonBestillingProgress, Long> {

    @Modifying
    Optional<OrganisasjonBestillingProgress> save(OrganisasjonBestillingProgress bestillingProgress);

    Optional<List<OrganisasjonBestillingProgress>> findByBestillingId(Long bestillingId);

    Optional<List<OrganisasjonBestilling>> findByBruker(Bruker bruker);

    @Transactional
    @Modifying
    int deleteByBestillingId(Long bestillingId);

    @Transactional
    @Modifying
    int deleteByOrganisasjonsnummer(String orgnummer);
}
