package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.repository.OrganisasjonBestillingProgressRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static no.nav.dolly.config.CachingConfig.CACHE_ORG_BESTILLING;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrganisasjonProgressService {

    private final OrganisasjonBestillingProgressRepository organisasjonProgressRepository;

    @Transactional
    @CacheEvict(value = CACHE_ORG_BESTILLING, allEntries = true)
    public Optional<OrganisasjonBestillingProgress> save(OrganisasjonBestillingProgress progress) {

        return organisasjonProgressRepository.save(progress);
    }

    @Transactional
    @CacheEvict(value = CACHE_ORG_BESTILLING, allEntries = true)
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
        Optional<List<OrganisasjonBestillingProgress>> bestillingProgress =
                organisasjonProgressRepository.findByBestillingId(bestillingsId);
        if (bestillingProgress.isEmpty()) {
            log.info("Fant ikke noen bestillingStatus med bestillingId: " + bestillingsId);
            return Collections.emptyList();
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
    @CacheEvict(value = CACHE_ORG_BESTILLING, allEntries = true)
    public void deleteByBestillingId(Long bestillingId) {
        organisasjonProgressRepository.deleteByBestillingId(bestillingId);
    }

    @Transactional
    @CacheEvict(value = CACHE_ORG_BESTILLING, allEntries = true)
    public void deleteByOrgnummer(String orgnummer) {
        organisasjonProgressRepository.deleteByOrganisasjonsnummer(orgnummer);
    }
}