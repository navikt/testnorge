package no.nav.api;

import ma.glasnost.orika.MapperFacade;
import no.nav.appserivces.tpsf.domain.request.RsBestillingProgress;
import no.nav.jpa.BestillingProgress;
import no.nav.service.BestillingProgressService;

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
    private BestillingProgressService service;

    @GetMapping("/{bestillingsId}")
    public List<RsBestillingProgress> checkBestillingsstatus(@PathVariable("bestillingsId") Long bestillingsId) {
        List<BestillingProgress> progress = service.fetchBestillingProgressByBestillingsIdFromDB(bestillingsId);
        return mapperFacade.mapAsList(progress, RsBestillingProgress.class);
    }

}
