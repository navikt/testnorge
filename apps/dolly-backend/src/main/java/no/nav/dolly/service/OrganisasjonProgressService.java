package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.OrganisasjonBestillingProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrganisasjonProgressService {

    private final OrganisasjonBestillingProgressRepository organisasjonProgressRepository;

    @Transactional
    public Mono<Void> setBestillingFeil(Long bestillingsId, String status) {


        return organisasjonProgressRepository.findByBestillingId(bestillingsId)
                .switchIfEmpty(Mono.error(
                        new NotFoundException("Fant ikke noen bestillingStatus med bestillingId: " + bestillingsId)))
                .doOnNext(progress -> progress.setOrganisasjonsforvalterStatus(status))
                .flatMap(organisasjonProgressRepository::save)
                .collectList()
                .then();
    }

    public Flux<OrganisasjonBestillingProgress> fetchOrganisasjonBestillingProgressByBestillingsId(Long bestillingsId) {

        return organisasjonProgressRepository.findByBestillingId(bestillingsId)
                .switchIfEmpty(Mono.error(new NotFoundException("Fant ingen status for bestillingId " + bestillingsId)));
    }

    public Flux<OrganisasjonBestillingProgress> findByOrganisasjonnummer(String orgnr) {

        return organisasjonProgressRepository.findByOrganisasjonsnummer(orgnr)
                .switchIfEmpty(Mono.error(new NotFoundException("Fant ingen status for Organisajonnummer " + orgnr)));
    }
}