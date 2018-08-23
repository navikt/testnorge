package no.nav.dolly.api;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.resultSet.RsBestilling;
import no.nav.dolly.domain.resultSet.RsBestillingProgress;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/bestilling", produces = MediaType.APPLICATION_JSON_VALUE)
public class BestillingController {

    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private BestillingProgressService progressService;

    @Autowired
    private BestillingService bestillingService;

    @GetMapping("/{bestillingsId}")
    public RsBestilling checkBestillingsstatus(@PathVariable("bestillingsId") Long bestillingsId) {
        List<RsBestillingProgress> progress = mapperFacade.mapAsList(progressService.fetchProgressButReturnEmptyListIfBestillingsIdIsNotFound(bestillingsId), RsBestillingProgress.class);
        RsBestilling rsBestilling = mapperFacade.map(bestillingService.fetchBestillingById(bestillingsId), RsBestilling.class);
        rsBestilling.setPersonStatus(progress);
        return rsBestilling;
    }

}
