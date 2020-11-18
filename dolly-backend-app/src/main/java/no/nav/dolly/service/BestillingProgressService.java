package no.nav.dolly.service;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.postgres.BestillingProgress;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.postgres.BestillingProgressRepository;

@Service
@RequiredArgsConstructor
public class BestillingProgressService {

    private final BestillingProgressRepository bestillingProgressRepository;

    public Optional<BestillingProgress> save(BestillingProgress progress) {

        return bestillingProgressRepository.save(progress);
    }

    public List<BestillingProgress> fetchBestillingProgressByBestillingsIdFromDB(Long bestillingsId) {
        return bestillingProgressRepository.findByBestillingId(bestillingsId).orElseThrow(
                () -> new NotFoundException("Kunne ikke finne bestillingsprogress med bestillingId=" + bestillingsId + ", i tabell T_BESTILLINGS_PROGRESS"));
    }

    public List<BestillingProgress> fetchBestillingProgressByBestillingId(Long bestillingsId) {

        return bestillingProgressRepository.findByBestillingId(bestillingsId).orElse(emptyList());
    }

    public List<BestillingProgress> fetchBestillingProgressByIdent(String ident) {
        return bestillingProgressRepository.findByIdent(ident);
    }
}