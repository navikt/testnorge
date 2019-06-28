package no.nav.dolly.service;

import static java.util.Objects.isNull;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BestillingProgressService {

    @Autowired
    private BestillingProgressRepository bestillingProgressRepository;


    public List<BestillingProgress> fetchBestillingProgressByBestillingsIdFromDB(Long bestillingsId) {
        List<BestillingProgress> progress = bestillingProgressRepository.findBestillingProgressByBestillingIdOrderByBestillingId(bestillingsId);

        if (isNull(progress) || progress.isEmpty()) {
            throw new NotFoundException("Kunne ikke finne bestillingsprogress med bestillingId=" + bestillingsId + ", i tabell T_BESTILLINGS_PROGRESS");
        }

        return progress;
    }

    public List<BestillingProgress> fetchBestillingProgressByBestillingId(Long bestillingsId) {
        return bestillingProgressRepository.findBestillingProgressByBestillingIdOrderByBestillingId(bestillingsId);
    }
}