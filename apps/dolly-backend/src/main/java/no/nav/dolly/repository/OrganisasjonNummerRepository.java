package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.OrganisasjonNummer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface OrganisasjonNummerRepository extends Repository<OrganisasjonNummer, Long> {

    Optional<OrganisasjonNummer> save(OrganisasjonNummer organisasjonNummer);

    Optional<List<OrganisasjonNummer>> findByBestillingId(Long bestillingId);

    @Query("from OrganisasjonNummer o where o.organisasjonsnr = :orgnummer")
    Optional<List<OrganisasjonNummer>> findByOrganisasjonNummer(String orgnummer);

    @Modifying
    int deleteByBestillingId(Long bestillingId);

    @Modifying
    int deleteByOrganisasjonsnr(String organisasjonsNummer);
}
