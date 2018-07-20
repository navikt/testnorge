package no.nav.service;

import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.exceptions.NotFoundException;
import no.nav.jpa.BestillingProgress;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static no.nav.util.UtilFunctions.isNullOrEmpty;

@Service
public class BestillingProgressService {

    @Autowired
    private BestillingProgressRepository repository;

    public List<BestillingProgress> fetchBestillingProgressByBestillingsIdFromDB(Long bestillingsId){
        List<BestillingProgress> progress = repository.findBestillingProgressByBestillingsId(bestillingsId);

        if(isNullOrEmpty(progress)){
            throw new NotFoundException("Kunne ikke finne bestillingsprogress med bestillingsId=" + bestillingsId + ", i tabell T_BESTILLINGS_PROGRESS");
        }

        return progress;
    }
}
