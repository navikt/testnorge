package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.OrganisasjonNummer;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.OrganisasjonNummerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganisasjonNummerService {

    private final OrganisasjonNummerRepository organisasjonNummerRepository;

    public Optional<OrganisasjonNummer> save(OrganisasjonNummer organisasjonNummer) {

        return organisasjonNummerRepository.save(organisasjonNummer);
    }

    public List<OrganisasjonNummer> fetchOrganisasjonNummereFromBestillingsId(Long bestillingsId) {
        return organisasjonNummerRepository.findByBestillingId(bestillingsId).orElseThrow(
                () -> new NotFoundException("Kunne ikke finne noen organisasjoner knyttet til bestillingId=" + bestillingsId + ", i tabell ORGANISASJON_BESTILLINGS_PROGRESS"));
    }

    public List<OrganisasjonNummer> fetchBestillingsIdFromOrganisasjonNummer(String orgnummer) {
        return organisasjonNummerRepository.findByOrganisasjonNummer(orgnummer).orElseThrow(
                () -> new NotFoundException("Kunne ikke finne noen bestillinger knyttet til organisasjonNummer=" + orgnummer + ", i tabell ORGANISASJON_BESTILLINGS_PROGRESS"));
    }

    @Transactional
    public void deleteByBestillingId(Long bestillingId) {
        organisasjonNummerRepository.deleteByBestillingId(bestillingId);
    }

    @Transactional
    public void deleteByOrgnummer(String orgnummer) {
        organisasjonNummerRepository.deleteByOrganisasjonsnr(orgnummer);
    }
}