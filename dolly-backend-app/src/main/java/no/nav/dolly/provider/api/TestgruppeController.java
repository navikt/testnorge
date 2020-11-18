package no.nav.dolly.provider.api;

import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;

import java.util.Set;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.service.DollyBestillingService;
import no.nav.dolly.bestilling.service.ImportAvPersonerFraTpsService;
import no.nav.dolly.bestilling.service.LeggTilPaaGruppeService;
import no.nav.dolly.bestilling.service.OpprettPersonerByKriterierService;
import no.nav.dolly.bestilling.service.OpprettPersonerFraIdenterMedKriterierService;
import no.nav.dolly.domain.jpa.postgres.Bestilling;
import no.nav.dolly.domain.jpa.postgres.Testgruppe;
import no.nav.dolly.domain.resultset.RsDollyBestillingFraIdenterRequest;
import no.nav.dolly.domain.resultset.RsDollyBestillingLeggTilPaaGruppe;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.RsDollyImportFraTpsRequest;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingStatus;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsLockTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsOpprettEndreTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppeMedBestillingId;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.TestgruppeService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v1/gruppe")
public class TestgruppeController {

    private final BestillingService bestillingService;
    private final DollyBestillingService dollyBestillingService;
    private final MapperFacade mapperFacade;
    private final ImportAvPersonerFraTpsService importAvPersonerFraTpsService;
    private final LeggTilPaaGruppeService leggTilPaaGruppeService;
    private final TestgruppeService testgruppeService;
    private final OpprettPersonerByKriterierService opprettPersonerByKriterierService;
    private final OpprettPersonerFraIdenterMedKriterierService opprettPersonerFraIdenterMedKriterierService;

    @CacheEvict(value = CACHE_GRUPPE, allEntries = true)
    @Transactional
    @PutMapping(value = "/{gruppeId}")
    @Operation(description = "Oppdater testgruppe")
    public RsTestgruppeMedBestillingId oppdaterTestgruppe(@PathVariable("gruppeId") Long gruppeId, @RequestBody RsOpprettEndreTestgruppe testgruppe) {
        Testgruppe gruppe = testgruppeService.oppdaterTestgruppe(gruppeId, testgruppe);
        return mapperFacade.map(gruppe, RsTestgruppeMedBestillingId.class);
    }

    @CacheEvict(value = CACHE_GRUPPE, allEntries = true)
    @Transactional
    @PutMapping(value = "/{gruppeId}/laas")
    @Operation(description = "Oppdater testgruppe Laas")
    public RsTestgruppe oppdaterTestgruppeLaas(@PathVariable("gruppeId") Long gruppeId,
            @RequestBody RsLockTestgruppe lockTestgruppe) {
        Testgruppe gruppe = testgruppeService.oppdaterTestgruppeMedLaas(gruppeId, lockTestgruppe);
        return mapperFacade.map(gruppe, RsTestgruppe.class);
    }

    @CacheEvict(value = { CACHE_GRUPPE }, allEntries = true)
    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "Opprett testgruppe")
    public RsTestgruppeMedBestillingId opprettTestgruppe(@RequestBody RsOpprettEndreTestgruppe createTestgruppeRequest) {
        Testgruppe gruppe = testgruppeService.opprettTestgruppe(createTestgruppeRequest);
        return mapperFacade.map(testgruppeService.fetchTestgruppeById(gruppe.getId()), RsTestgruppeMedBestillingId.class);
    }

    @Cacheable(CACHE_GRUPPE)
    @GetMapping("/{gruppeId}")
    @Operation(description = "Hent testgruppe")
    public RsTestgruppeMedBestillingId getTestgruppe(@PathVariable("gruppeId") Long gruppeId) {
        return mapperFacade.map(testgruppeService.fetchTestgruppeById(gruppeId), RsTestgruppeMedBestillingId.class);
    }

    @Cacheable(CACHE_GRUPPE)
    @GetMapping
    @Operation(description = "Hent testgrupper")
    public Set<RsTestgruppe> getTestgrupper(
            @RequestParam(name = "brukerId", required = false) String brukerId) {
        return mapperFacade.mapAsSet(testgruppeService.getTestgruppeByBrukerId(brukerId), RsTestgruppe.class);
    }

    @CacheEvict(value = CACHE_GRUPPE, allEntries = true)
    @Transactional
    @DeleteMapping("/{gruppeId}")
    @Operation(description = "Slett gruppe")
    public void slettgruppe(@PathVariable("gruppeId") Long gruppeId) {

        testgruppeService.deleteGruppeById(gruppeId);
    }

    @CacheEvict(value = { CACHE_BESTILLING, CACHE_GRUPPE }, allEntries = true)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{gruppeId}/bestilling")
    @Operation(description = "Opprett berikede testpersoner basert på fødselsdato, kjønn og identtype")
    public RsBestillingStatus opprettIdentBestilling(@PathVariable("gruppeId") Long gruppeId, @RequestBody RsDollyBestillingRequest request) {
        Bestilling bestilling = bestillingService.saveBestilling(gruppeId, request, request.getTpsf(), request.getAntall(), null);

        opprettPersonerByKriterierService.executeAsync(bestilling);
        return mapperFacade.map(bestilling, RsBestillingStatus.class);
    }

    @Operation(description = "Opprett berikede testpersoner basert på eskisterende identer")
    @CacheEvict(value = { CACHE_BESTILLING, CACHE_GRUPPE }, allEntries = true)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{gruppeId}/bestilling/fraidenter")
    public RsBestillingStatus opprettIdentBestillingFraIdenter(@PathVariable("gruppeId") Long gruppeId, @RequestBody RsDollyBestillingFraIdenterRequest request) {
        Bestilling bestilling = bestillingService.saveBestilling(gruppeId, request, request.getTpsf(),
                request.getOpprettFraIdenter().size(), request.getOpprettFraIdenter());

        opprettPersonerFraIdenterMedKriterierService.executeAsync(bestilling);
        return mapperFacade.map(bestilling, RsBestillingStatus.class);
    }

    @Operation(description = "Importere testpersoner fra TPS og legg til berikning non-TPS artifacter")
    @CacheEvict(value = { CACHE_BESTILLING, CACHE_GRUPPE }, allEntries = true)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{gruppeId}/bestilling/importFraTps")
    public RsBestillingStatus importAvIdenterBestilling(@PathVariable("gruppeId") Long gruppeId, @RequestBody RsDollyImportFraTpsRequest request) {

        Bestilling bestilling = bestillingService.saveBestilling(gruppeId, request);

        importAvPersonerFraTpsService.executeAsync(bestilling);
        return mapperFacade.map(bestilling, RsBestillingStatus.class);
    }

    @Operation(description = "Legg til berikning på alle i gruppe")
    @CacheEvict(value = { CACHE_BESTILLING, CACHE_GRUPPE }, allEntries = true)
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{gruppeId}/leggtilpaagruppe")
    public RsBestillingStatus endreGruppeLeggTil(@PathVariable("gruppeId") Long gruppeId, @RequestBody RsDollyBestillingLeggTilPaaGruppe request) {

        Bestilling bestilling = bestillingService.saveBestilling(gruppeId, request);

        leggTilPaaGruppeService.executeAsync(bestilling);
        return mapperFacade.map(bestilling, RsBestillingStatus.class);
    }
}