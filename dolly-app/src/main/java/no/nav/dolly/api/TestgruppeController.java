package no.nav.dolly.api;

import static java.lang.String.format;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.service.DollyBestillingService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsBestilling;
import no.nav.dolly.domain.resultset.RsDollyBestillingsRequest;
import no.nav.dolly.domain.resultset.RsOpprettEndreTestgruppe;
import no.nav.dolly.domain.resultset.RsTestgruppe;
import no.nav.dolly.domain.resultset.RsTestgruppeUtvidet;
import no.nav.dolly.domain.resultset.RsTestident;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TestgruppeService;

@RestController
@RequestMapping(value = "api/v1/gruppe")
public class TestgruppeController {

    @Autowired
    private TestgruppeService testgruppeService;

    @Autowired
    private IdentService identService;

    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private DollyBestillingService dollyBestillingService;

    @Autowired
    private BestillingService bestillingService;

    @CacheEvict(value = "gruppe", allEntries = true)
    @Transactional
    @PutMapping(value = "/{gruppeId}")
    public RsTestgruppeUtvidet oppdaterTestgruppe(@PathVariable("gruppeId") Long gruppeId, @RequestBody RsOpprettEndreTestgruppe testgruppe) {
        Testgruppe gruppe = testgruppeService.oppdaterTestgruppe(gruppeId, testgruppe);
        return mapperFacade.map(gruppe, RsTestgruppeUtvidet.class);
    }

    @CacheEvict(value = { "gruppe", "team" }, allEntries = true)
    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public RsTestgruppeUtvidet opprettTestgruppe(@RequestBody RsOpprettEndreTestgruppe createTestgruppeRequest) {
        Testgruppe gruppe = testgruppeService.opprettTestgruppe(createTestgruppeRequest);
        return mapperFacade.map(testgruppeService.fetchTestgruppeById(gruppe.getId()), RsTestgruppeUtvidet.class);
    }

    @CacheEvict(value = "gruppe", allEntries = true)
    @Transactional
    @PutMapping("/{gruppeId}/slettTestidenter")
    public void deleteTestident(@RequestBody List<RsTestident> testpersonIdentListe) {
        identService.slettTestidenter(testpersonIdentListe);
    }

    @CacheEvict(value = "gruppe", allEntries = true)
    @Transactional
    @DeleteMapping("/{gruppeId}/slettTestident")
    public void deleteTestident(@RequestParam String ident) {
        if (identService.slettTestident(ident) == 0) {
            throw new NotFoundException(format("Testperson med ident %s ble ikke funnet.", ident));
        }
    }

    @Cacheable("gruppe")
    @GetMapping("/{gruppeId}")
    public RsTestgruppeUtvidet getTestgruppe(@PathVariable("gruppeId") Long gruppeId) {
        return mapperFacade.map(testgruppeService.fetchTestgruppeById(gruppeId), RsTestgruppeUtvidet.class);
    }

    @Cacheable("gruppe")
    @GetMapping
    public List<RsTestgruppe> getTestgrupper(
            @RequestParam(name = "navIdent", required = false) String navIdent,
            @RequestParam(name = "teamId", required = false) Long teamId) {
        return mapperFacade.mapAsList(testgruppeService.getTestgruppeByNavidentOgTeamId(navIdent, teamId), RsTestgruppe.class);
    }

    @CacheEvict(value = "gruppe", allEntries = true)
    @Transactional
    @DeleteMapping("/{gruppeId}")
    public void slettgruppe(@PathVariable("gruppeId") Long gruppeId) {
        if (testgruppeService.slettGruppeById(gruppeId) == 0) {
            throw new NotFoundException(format("Gruppe med id %s ble ikke funnet.", gruppeId));
        }
    }

    @CacheEvict(value = "gruppe", allEntries = true)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{gruppeId}/bestilling")
    public RsBestilling opprettIdentBestilling(@PathVariable("gruppeId") Long gruppeId, @RequestBody RsDollyBestillingsRequest request) {
        Bestilling bestilling = bestillingService.saveBestillingByGruppeIdAndAntallIdenter(gruppeId, request.getAntall(), request.getEnvironments());

        dollyBestillingService.opprettPersonerByKriterierAsync(gruppeId, request, bestilling.getId());
        return mapperFacade.map(bestilling, RsBestilling.class);
    }

    @Cacheable("gruppe")
    @GetMapping("/{gruppeId}/identer")
    public List<String> getIdentsByGroupId(@PathVariable("gruppeId") Long gruppeId) {
        return testgruppeService.fetchIdenterByGruppeId(gruppeId);
    }
}
