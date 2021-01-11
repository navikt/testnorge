package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.OrganisasjonNummer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface OrganisasjonNummerRepository extends Repository<OrganisasjonNummer, Long> {

    Optional<OrganisasjonNummer> save(OrganisasjonNummer organisasjonNummer);

    Optional<List<OrganisasjonNummer>> findByBestillingId(Long bestillingId);

    @Modifying
    int deleteByBestillingId(Long bestillingId);

    @Modifying
    int deleteByOrganisasjonsnr(Long organisasjonsNummer);
}
