package no.nav.dolly.service;

import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.domain.jpa.BestillingProgress;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static no.nav.dolly.util.UtilFunctions.isNullOrEmpty;

@Service
public class BestillingProgressService {

    @Autowired
    private BestillingProgressRepository repository;

    public List<BestillingProgress> fetchBestillingProgressByBestillingsIdFromDB(Long bestillingsId){
        List<BestillingProgress> progress = repository.findBestillingProgressByBestillingId(bestillingsId);

        if(isNullOrEmpty(progress)){
            throw new NotFoundException("Kunne ikke finne bestillingsprogress med bestillingId=" + bestillingsId + ", i tabell T_BESTILLINGS_PROGRESS");
        }

        return progress;
    }

    public List<BestillingProgress> fetchProgressButReturnEmptyListIfBestillingsIdIsNotFound(Long bestillingsId){
        List<BestillingProgress> progresses = repository.findBestillingProgressByBestillingId(bestillingsId);
        return progresses;
    }
}
