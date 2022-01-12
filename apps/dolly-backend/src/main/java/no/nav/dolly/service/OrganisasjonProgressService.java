package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.OrganisasjonBestillingProgressRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrganisasjonProgressService {

    private final OrganisasjonBestillingProgressRepository organisasjonProgressRepository;
    private final BrukerRepository brukerRepository;

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
        Optional<List<OrganisasjonBestillingProgress>> bestillingProgress =
                organisasjonProgressRepository.findByBestillingId(bestillingsId);
        if (bestillingProgress.isEmpty()) {
            log.info("Fant ikke noen bestillingStatus med bestillingId: " + bestillingsId);
            return Collections.emptyList();
        }
        return bestillingProgress.get();
    }

    public List<OrganisasjonBestilling> fetchOrganisasjonBestillingProgressByBrukerId(String brukerId) {

        var bruker = brukerRepository.findBrukerByBrukerId(brukerId)
                .orElseThrow(() -> new NotFoundException("Bruker ikke funnet med id " + brukerId ));

        return organisasjonProgressRepository.findByBruker(bruker)
                        .orElseThrow(() -> new NotFoundException("Bestilling ikke funnet for bruker " + brukerId));
//
//        if (bestillingProgress.isEmpty()) {
//            throw new NotFoundException(
//                    "Fant ikke noen bestillingStatus med brukerId: " + brukerId);
//        }
//        return bestillingProgress.get();
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