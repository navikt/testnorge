package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.repository.OrganisasjonBestillingProgressRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganisasjonProgressService {

    private final OrganisasjonBestillingProgressRepository organisasjonProgressRepository;

    @Transactional
    public Optional<OrganisasjonBestillingProgress> save(OrganisasjonBestillingProgress progress) {

        return organisasjonProgressRepository.save(progress);
    }

    public List<OrganisasjonBestillingProgress> fetchOrganisasjonBestillingProgressByBestillingsId(Long bestillingsId) {
        Optional<List<OrganisasjonBestillingProgress>> bestillingProgress =
                organisasjonProgressRepository.findByBestillingId(bestillingsId);
        if (bestillingProgress.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND,
                    "Fant ikke noen bestillingStatus med bestillingId: " + bestillingsId);
        }
        return bestillingProgress.get();
    }

    public List<OrganisasjonBestillingProgress> fetchOrganisasjonBestillingProgressByBrukerId(String brukerId) {

        Optional<List<OrganisasjonBestillingProgress>> bestillingProgress =
                organisasjonProgressRepository.findbyBrukerId(brukerId);
        if (bestillingProgress.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND,
                    "Fant ikke noen bestillingStatus med brukerId: " + brukerId);
        }
        return bestillingProgress.get();
    }

    @Transactional
    public void deleteByBestillingId(Long bestillingId) {
        organisasjonProgressRepository.deleteByBestillingId(bestillingId);
    }
}