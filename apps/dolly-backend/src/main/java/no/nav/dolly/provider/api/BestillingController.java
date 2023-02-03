package no.nav.dolly.provider.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.service.GjenopprettBestillingService;
import no.nav.dolly.domain.MalbestillingNavn;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingFragment;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingStatus;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingWrapper;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingWrapper.RsMalBestilling;
import no.nav.dolly.domain.resultset.entity.testident.RsWhereAmI;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.MalBestillingService;
import no.nav.dolly.service.NavigasjonService;
import no.nav.dolly.service.OrganisasjonBestillingService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/bestilling", produces = MediaType.APPLICATION_JSON_VALUE)
public class BestillingController {

    private final MapperFacade mapperFacade;
    private final BestillingService bestillingService;
    private final OrganisasjonBestillingService organisasjonBestillingService;
    private final NavigasjonService navigasjonService;
    private final MalBestillingService malBestillingService;
    private final GjenopprettBestillingService gjenopprettBestillingService;

    @Cacheable(value = CACHE_BESTILLING)
    @GetMapping("/{bestillingId}")
    @Operation(description = "Hent Bestilling med bestillingsId")
    public RsBestillingStatus getBestillingById(@PathVariable("bestillingId") Long bestillingId) {
        return mapperFacade.map(bestillingService.fetchBestillingById(bestillingId), RsBestillingStatus.class);
    }

    @GetMapping("/soekBestilling")
    @Operation(description = "Hent Bestillinger basert på fragment")
    public List<RsBestillingFragment> getBestillingerByFragment(@RequestParam(value = "fragment") String fragment) {
        return bestillingService.fetchBestillingByFragment(fragment);
    }

    @Operation(description = "Naviger til ønsket bestilling")
    @Transactional
    @GetMapping("/naviger/{bestillingId}")
    public Mono<RsWhereAmI> navigerTilBestilling(@PathVariable Long bestillingId) {

        return navigasjonService.navigerTilBestilling(bestillingId);
    }

    @Cacheable(value = CACHE_BESTILLING)
    @GetMapping("/gruppe/{gruppeId}")
    @Operation(description = "Hent Bestillinger tilhørende en gruppe med gruppeId")
    public List<RsBestillingStatus> getBestillinger(@PathVariable("gruppeId") Long gruppeId,
                                                    @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                    @RequestParam(value = "pageSize", required = false, defaultValue = "2000") Integer pageSize) {
        var bestillinger = bestillingService.getBestillingerFromGruppePaginert(gruppeId, page, pageSize);
        if (nonNull(bestillinger) && !bestillinger.isEmpty()) {
            return mapperFacade.mapAsList(bestillinger.toList(), RsBestillingStatus.class);
        }
        return mapperFacade.mapAsList(emptyList(), RsBestillingStatus.class);
    }

    @GetMapping("/gruppe/{gruppeId}/ikkeferdig")
    @Operation(description = "Hent Bestillinger tilhørende en gruppe med gruppeId")
    public List<RsBestillingStatus> getIkkeFerdigBestillinger(@PathVariable("gruppeId") Long gruppeId) {
        return mapperFacade.mapAsList(bestillingService.fetchBestillingerByGruppeIdOgIkkeFerdig(gruppeId), RsBestillingStatus.class);
    }

    @CacheEvict(value = { CACHE_BESTILLING, CACHE_GRUPPE }, allEntries = true)
    @DeleteMapping("/stop/{bestillingId}")
    @Operation(description = "Stopp en Bestilling med bestillingsId")
    public RsBestillingStatus stopBestillingProgress(@PathVariable("bestillingId") Long bestillingId, @RequestParam(value = "organisasjonBestilling", required = false) Boolean organisasjonBestilling) {

        return isTrue(organisasjonBestilling)
                ? mapperFacade.map(organisasjonBestillingService.cancelBestilling(bestillingId), RsBestillingStatus.class)
                : mapperFacade.map(bestillingService.cancelBestilling(bestillingId), RsBestillingStatus.class);
    }

    @CacheEvict(value = { CACHE_BESTILLING, CACHE_GRUPPE }, allEntries = true)
    @PostMapping("/gjenopprett/{bestillingId}")
    @Operation(description = "Gjenopprett en bestilling med bestillingsId, for en liste med miljoer")
    public RsBestillingStatus gjenopprettBestilling(@PathVariable("bestillingId") Long bestillingId, @RequestParam(value = "miljoer", required = false) String miljoer) {
        Bestilling bestilling = bestillingService.createBestillingForGjenopprettFraBestilling(bestillingId, nonNull(miljoer) ? asList(miljoer.split(",")) : emptyList());
        gjenopprettBestillingService.executeAsync(bestilling);
        return mapperFacade.map(bestilling, RsBestillingStatus.class);
    }

    @Cacheable(value = CACHE_BESTILLING)
    @GetMapping("/malbestilling")
    @Operation(description = "Hent mal-bestilling")
    public RsMalBestillingWrapper getMalBestillinger() {

        return malBestillingService.getMalBestillinger();
    }

    @GetMapping("/malbestilling/bruker")
    @Operation(description = "Hent mal-bestillinger for en spesifikk bruker, kan filtreres på malnavn")
    public List<RsMalBestilling> getMalbestillingByNavn(@RequestParam(value = "brukerId") String brukerId, @RequestParam(name = "malNavn", required = false) String malNavn) {

        return malBestillingService.getMalbestillingByNavnAndUser(brukerId, malNavn);
    }

    @CacheEvict(value = { CACHE_BESTILLING }, allEntries = true)
    @DeleteMapping("/malbestilling/{id}")
    @Operation(description = "Slett mal-bestilling")
    public void deleteMalBestilling(@PathVariable Long id) {

        bestillingService.redigerBestilling(id, null);
    }

    @CacheEvict(value = { CACHE_BESTILLING }, allEntries = true)
    @PutMapping("/malbestilling/{id}")
    @Operation(description = "Rediger mal-bestilling")
    public void redigerMalBestilling(@PathVariable Long id, @RequestBody MalbestillingNavn malbestillingNavn) {

        bestillingService.redigerBestilling(id, malbestillingNavn.getMalNavn());
    }
}
