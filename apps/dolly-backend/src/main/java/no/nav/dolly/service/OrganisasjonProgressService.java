package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.OrganisasjonBestillingProgressRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrganisasjonProgressService {

    private final OrganisasjonBestillingProgressRepository organisasjonProgressRepository;

    @Transactional
    public Optional<OrganisasjonBestillingProgress> save(OrganisasjonBestillingProgress progress) {

        return organisasjonProgressRepository.save(progress);
    }

    @Transactional
    public void setBestillingFeil(Long bestillingsId, String status) {

        Optional<List<OrganisasjonBestillingProgress>> bestillingProgress =
                organisasjonProgressRepository.findByBestillingId(bestillingsId);
        if (bestillingProgress.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND,
                    "Fant ikke noen bestillingStatus med bestillingId: " + bestillingsId);
        }

        bestillingProgress.get().get(0).setOrganisasjonsforvalterStatus(status);

        organisasjonProgressRepository.save(bestillingProgress.get().get(0));
    }

    public List<OrganisasjonBestillingProgress> fetchOrganisasjonBestillingProgressByBestillingsId(Long bestillingsId) {

        return organisasjonProgressRepository.findByBestillingId(bestillingsId)
                .orElseThrow(() -> new NotFoundException("Fant ingen status for bestillingId " + bestillingsId));
    }

    public List<OrganisasjonBestillingProgress> findByOrganisasjonnummer(String orgnr) {

        return organisasjonProgressRepository.findByOrganisasjonsnummer(orgnr)
                .orElseThrow(() -> new NotFoundException("Organisajonnummer ikke funnet i database " + orgnr));
    }

    @Transactional
    public void deleteByBestillingId(Long bestillingId) {
        organisasjonProgressRepository.deleteByBestillingId(bestillingId);
    }

    @Transactional
    public void deleteByOrgnummer(String orgnummer) {
        organisasjonProgressRepository.deleteByOrganisasjonsnummer(orgnummer);
    }
}