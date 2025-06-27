package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BestillingProgressService {

    private final BestillingProgressRepository bestillingProgressRepository;

    public Mono<BestillingProgress> save(BestillingProgress progress) {

        return bestillingProgressRepository.save(progress);
    }

    public Flux<BestillingProgress> fetchBestillingProgressByBestillingsIdFromDB(Long bestillingsId) {

        return bestillingProgressRepository.findByBestillingId(bestillingsId)
                .switchIfEmpty(Mono.error(
                        new NotFoundException("Kunne ikke finne bestillingsprogress med bestillingId=" + bestillingsId)));
    }

    public Flux<BestillingProgress> fetchBestillingProgressByBestillingId(Long bestillingsId) {

        return bestillingProgressRepository.findByBestillingId(bestillingsId);
    }

    @Transactional
    public Mono<BestillingProgress> swapIdent(String oldIdent, String newIdent) {

        return bestillingProgressRepository.swapIdent(oldIdent, newIdent);
    }
}