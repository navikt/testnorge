package no.nav.dolly.service;

import static no.nav.dolly.util.UtilFunctions.isNullOrEmpty;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsTestident;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingProgressRepository;

@Service
public class BestillingProgressService {

    @Autowired
    private BestillingProgressRepository repository;

    public List<BestillingProgress> fetchBestillingProgressByBestillingsIdFromDB(Long bestillingsId) {
        List<BestillingProgress> progress = repository.findBestillingProgressByBestillingId(bestillingsId);

        if (isNullOrEmpty(progress)) {
            throw new NotFoundException("Kunne ikke finne bestillingsprogress med bestillingId=" + bestillingsId + ", i tabell T_BESTILLINGS_PROGRESS");
        }

        return progress;
    }

    public List<BestillingProgress> fetchProgressButReturnEmptyListIfBestillingsIdIsNotFound(Long bestillingsId) {
        return repository.findBestillingProgressByBestillingId(bestillingsId);
    }

    public List<BestillingProgress> fetchBestillingsProgressByIdentId(List<RsTestident> rsidenter) {
        List<String> testidents = new ArrayList<>(rsidenter.size());
        for (RsTestident ident : rsidenter) {
            testidents.add(ident.getIdent());
        }
        return repository.findBestillingProgressByIdentIn(testidents);
    }
}
