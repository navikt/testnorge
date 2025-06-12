package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class BestillingProgressService {

    private final BestillingProgressRepository bestillingProgressRepository;

    public Mono<BestillingProgress> save(BestillingProgress progress) {

        return bestillingProgressRepository.save(progress);
    }

    public List<BestillingProgress> fetchBestillingProgressByBestillingsIdFromDB(Long bestillingsId) {
        return bestillingProgressRepository.findByBestillingId(bestillingsId).orElseThrow(
                () -> new NotFoundException("Kunne ikke finne bestillingsprogress med bestillingId=" + bestillingsId + ", i tabell T_BESTILLINGS_PROGRESS"));
    }

    public List<BestillingProgress> fetchBestillingProgressByBestillingId(Long bestillingsId) {

        return bestillingProgressRepository.findByBestillingId(bestillingsId).orElse(emptyList());
    }

    @Transactional
    public void swapIdent(String oldIdent, String newIdent) {

        bestillingProgressRepository.swapIdent(oldIdent, newIdent);
    }
}