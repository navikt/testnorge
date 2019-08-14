package no.nav.dolly.provider.api;

import static java.lang.String.format;
import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;
import static no.nav.dolly.config.CachingConfig.CACHE_TEAM;
import static no.nav.dolly.provider.api.documentation.DocumentationNotes.BESTILLING_BESKRIVELSE;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.service.DollyBestillingService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsBestilling;
import no.nav.dolly.domain.resultset.RsDollyBestillingFraIdenterRequest;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.RsOpprettEndreTestgruppe;
import no.nav.dolly.domain.resultset.RsTestgruppe;
import no.nav.dolly.domain.resultset.RsTestgruppeUtvidet;
import no.nav.dolly.domain.resultset.RsTestident;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TestgruppeService;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v1/gruppe")
public class TestgruppeController {

    private final TestgruppeService testgruppeService;
    private final IdentService identService;
    private final MapperFacade mapperFacade;
    private final DollyBestillingService dollyBestillingService;
    private final BestillingService bestillingService;

    @Cacheable(CACHE_GRUPPE)
    @GetMapping
    @ApiOperation("Hent Testgrupper tilhørende navIdent eller teamId")
    public List<RsTestgruppe> getTestgrupper(
            @RequestParam(name = "navIdent", required = false) String navIdent,
            @RequestParam(name = "teamId", required = false) Long teamId) {
        return mapperFacade.mapAsList(testgruppeService.getTestgruppeByNavidentOgTeamId(navIdent, teamId), RsTestgruppe.class);
    }

    @Cacheable(CACHE_GRUPPE)
    @GetMapping("/{gruppeId}")
    @ApiOperation("Hent Testgruppe med gruppeId")
    public RsTestgruppeUtvidet getTestgruppe(@PathVariable("gruppeId") Long gruppeId) {
        return mapperFacade.map(testgruppeService.fetchTestgruppeById(gruppeId), RsTestgruppeUtvidet.class);
    }

    @Cacheable(CACHE_GRUPPE)
    @GetMapping("/{gruppeId}/identer")
    @ApiOperation("Hent identer tilknyttet en gruppe")
    public List<String> getIdentsByGroupId(@PathVariable("gruppeId") Long gruppeId) {
        return testgruppeService.fetchIdenterByGruppeId(gruppeId);
    }

    @Transactional
    @CacheEvict(value = {CACHE_GRUPPE, CACHE_TEAM}, allEntries = true)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Opprett Testgruppe")
    public RsTestgruppeUtvidet opprettTestgruppe(@RequestBody RsOpprettEndreTestgruppe createTestgruppeRequest) {
        Testgruppe gruppe = testgruppeService.opprettTestgruppe(createTestgruppeRequest);
        return mapperFacade.map(testgruppeService.fetchTestgruppeById(gruppe.getId()), RsTestgruppeUtvidet.class);
    }

    @CacheEvict(value = {CACHE_BESTILLING, CACHE_GRUPPE}, allEntries = true)
    @PostMapping("/{gruppeId}/bestilling")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Opprett identer i TPS basert på fødselsdato, kjønn og identtype", notes = BESTILLING_BESKRIVELSE)
    public RsBestilling opprettIdentBestilling(@PathVariable("gruppeId") Long gruppeId, @RequestBody RsDollyBestillingRequest request) {
        Bestilling bestilling = bestillingService.saveBestilling(gruppeId, request, request.getTpsf(), request.getAntall(), null);

        dollyBestillingService.opprettPersonerByKriterierAsync(gruppeId, request, bestilling);
        return mapperFacade.map(bestilling, RsBestilling.class);
    }

    @CacheEvict(value = {CACHE_BESTILLING, CACHE_GRUPPE}, allEntries = true)
    @PostMapping("/{gruppeId}/bestilling/fraidenter")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Opprett identer i TPS fra ekisterende identer", notes = BESTILLING_BESKRIVELSE)
    public RsBestilling opprettIdentBestillingFraIdenter(@PathVariable("gruppeId") Long gruppeId, @RequestBody RsDollyBestillingFraIdenterRequest request) {
        Bestilling bestilling = bestillingService.saveBestilling(gruppeId, request, request.getTpsf(),
                request.getOpprettFraIdenter().size(), request.getOpprettFraIdenter());

        dollyBestillingService.opprettPersonerFraIdenterMedKriterierAsync(gruppeId, request, bestilling);
        return mapperFacade.map(bestilling, RsBestilling.class);
    }

    @Transactional
    @CacheEvict(value = CACHE_GRUPPE, allEntries = true)
    @PutMapping(value = "/{gruppeId}")
    @ApiOperation("Oppdater informasjon om Testgruppe")
    public RsTestgruppeUtvidet oppdaterTestgruppe(@PathVariable("gruppeId") Long gruppeId, @RequestBody RsOpprettEndreTestgruppe testgruppe) {
        Testgruppe gruppe = testgruppeService.oppdaterTestgruppe(gruppeId, testgruppe);
        return mapperFacade.map(gruppe, RsTestgruppeUtvidet.class);
    }

    @Transactional
    @CacheEvict(value = CACHE_GRUPPE, allEntries = true)
    @PutMapping("/{gruppeId}/slettTestidenter")
    @ApiOperation("Fjern en eller flere Testident fra en Testgruppe")
    public void deleteTestident(@RequestBody List<RsTestident> testpersonIdentListe) {
        identService.slettTestidenter(testpersonIdentListe);
    }

    @Transactional
    @CacheEvict(value = CACHE_GRUPPE, allEntries = true)
    @DeleteMapping("/{gruppeId}")
    @ApiOperation("Slett Testgruppe")
    public void slettgruppe(@PathVariable("gruppeId") Long gruppeId) {
        if (testgruppeService.slettGruppeById(gruppeId) == 0) {
            throw new NotFoundException(format("Gruppe med id %s ble ikke funnet.", gruppeId));
        }
    }

    @Transactional
    @CacheEvict(value = CACHE_GRUPPE, allEntries = true)
    @DeleteMapping("/{gruppeId}/slettTestident")
    @ApiOperation("Slett enkelt Testident fra Testgruppe")
    public void deleteTestident(@RequestParam String ident) {
        if (identService.slettTestident(ident) == 0) {
            throw new NotFoundException(format("Testperson med ident %s ble ikke funnet.", ident));
        }
    }
}
