package no.nav.dolly.api;

import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.RsBestilling;
import no.nav.dolly.domain.resultset.RsBestillingProgress;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;

@Transactional
@RestController
@RequestMapping(value = "/api/v1/bestilling", produces = MediaType.APPLICATION_JSON_VALUE)
public class BestillingController {

    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private BestillingProgressService progressService;

    @Autowired
    private BestillingService bestillingService;

    @Cacheable(value = CACHE_BESTILLING)
    @GetMapping("/{bestillingId}")
    public RsBestilling checkBestillingsstatus(@PathVariable("bestillingId") Long bestillingId) {
        List<RsBestillingProgress> progress = mapperFacade.mapAsList(progressService.fetchBestillingProgressByBestillingId(bestillingId), RsBestillingProgress.class);
        RsBestilling rsBestilling = mapperFacade.map(bestillingService.fetchBestillingById(bestillingId), RsBestilling.class);
        rsBestilling.setBestillingProgress(progress);
        return rsBestilling;
    }

    @Cacheable(value = CACHE_BESTILLING)
    @GetMapping("/gruppe/{gruppeId}")
    public List<RsBestilling> getBestillinger(@PathVariable("gruppeId") Long gruppeId) {
        List<RsBestilling> bestillinger = mapperFacade.mapAsList(bestillingService.fetchBestillingerByGruppeId(gruppeId), RsBestilling.class);
        bestillinger.forEach(rsBestilling -> rsBestilling.setBestillingProgress(
                mapperFacade.mapAsList(progressService.fetchBestillingProgressByBestillingId(rsBestilling.getId()), RsBestillingProgress.class)));
        return bestillinger;
    }

    @CacheEvict(value = CACHE_GRUPPE, allEntries = true)
    @DeleteMapping("/stop/{bestillingId}")
    public RsBestilling stopBestillingProgress(@PathVariable("bestillingId") Long bestillingId) {
        Bestilling bestilling = bestillingService.cancelBestilling(bestillingId);
        return mapperFacade.map(bestilling, RsBestilling.class);
    }
}