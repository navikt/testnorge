package no.nav.dolly.api;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.appserivces.tpsf.service.DollyTpsfService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.RsBestilling;
import no.nav.dolly.domain.resultset.RsDollyBestillingsRequest;
import no.nav.dolly.domain.resultset.RsOpprettTestgruppe;
import no.nav.dolly.domain.resultset.RsTestgruppe;
import no.nav.dolly.domain.resultset.RsTestgruppeMedErMedlemOgFavoritt;
import no.nav.dolly.domain.resultset.RsTestident;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TestgruppeService;

import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private DollyTpsfService dollyTpsfService;

    @Autowired
    private BestillingService bestillingService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public RsTestgruppeMedErMedlemOgFavoritt opprettTestgruppe(@RequestBody RsOpprettTestgruppe createTestgruppeRequest) {
        RsTestgruppe gruppe = testgruppeService.opprettTestgruppe(createTestgruppeRequest);
        return testgruppeService.rsTestgruppeToRsTestgruppeMedMedlemOgFavoritt(gruppe);
    }

    @PutMapping(value = "/{gruppeId}")
    public RsTestgruppeMedErMedlemOgFavoritt oppdaterTestgruppe(@PathVariable("gruppeId") Long gruppeId, @RequestBody RsOpprettTestgruppe testgruppe) {
        RsTestgruppe gruppe = testgruppeService.oppdaterTestgruppe(gruppeId, testgruppe);
        return testgruppeService.rsTestgruppeToRsTestgruppeMedMedlemOgFavoritt(gruppe);
    }

    @PutMapping("/{gruppeId}/slettTestidenter")
    public void deleteTestident(@RequestBody List<RsTestident> testpersonIdentListe) {
        identService.slettTestidenter(testpersonIdentListe);
    }

    @GetMapping("/{gruppeId}")
    public RsTestgruppeMedErMedlemOgFavoritt getTestgruppe(@PathVariable("gruppeId") Long gruppeId) {
        RsTestgruppe gruppe = mapperFacade.map(testgruppeService.fetchTestgruppeById(gruppeId), RsTestgruppe.class);
        RsTestgruppeMedErMedlemOgFavoritt gruppeMedMedlemOgFav = testgruppeService.rsTestgruppeToRsTestgruppeMedMedlemOgFavoritt(gruppe);
        gruppeMedMedlemOgFav.setBestillinger(mapperFacade.mapAsList(bestillingService.fetchBestillingerByGruppeId(gruppeId), RsBestilling.class));
        return gruppeMedMedlemOgFav;
    }

    @GetMapping
    public Set<RsTestgruppeMedErMedlemOgFavoritt> getTestgrupper(
            @RequestParam(name = "navIdent", required = false) String navIdent,
            @RequestParam(name = "teamId", required = false) Long teamId
    ) {
        return testgruppeService.getTestgruppeByNavidentOgTeamId(navIdent, teamId);
    }

    @DeleteMapping("/{gruppeId}")
    public void slettgruppe(@PathVariable("gruppeId") Long gruppeId){
        testgruppeService.slettGruppeById(gruppeId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{gruppeId}/bestilling")
    public RsBestilling oppretteIdentBestilling(@PathVariable("gruppeId") Long gruppeId, @RequestBody RsDollyBestillingsRequest request) {
        Bestilling bestilling = bestillingService.saveBestillingByGruppeIdAndAntallIdenter(gruppeId, request.getAntall(), request.getEnvironments());
        dollyTpsfService.opprettPersonerByKriterierAsync(gruppeId, request, bestilling.getId());
        return mapperFacade.map(bestilling, RsBestilling.class);
    }
}
