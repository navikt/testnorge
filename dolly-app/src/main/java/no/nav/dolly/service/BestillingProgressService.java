package no.nav.dolly.service;

import static java.util.Objects.isNull;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingProgressRepository;

@Service
public class BestillingProgressService {

    @Autowired
    private BestillingProgressRepository repository;

    public List<BestillingProgress> fetchBestillingProgressByBestillingsIdFromDB(Long bestillingsId) {
        List<BestillingProgress> progress = repository.findBestillingProgressByBestillingIdOrderByBestillingId(bestillingsId);

        if (isNull(progress) || progress.isEmpty()) {
            throw new NotFoundException("Kunne ikke finne bestillingsprogress med bestillingId=" + bestillingsId + ", i tabell T_BESTILLINGS_PROGRESS");
        }

        return progress;
    }

    public List<BestillingProgress> fetchProgressButReturnEmptyListIfBestillingsIdIsNotFound(Long bestillingsId) {
        return repository.findBestillingProgressByBestillingIdOrderByBestillingId(bestillingsId);
    }
}
