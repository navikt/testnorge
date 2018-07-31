package no.nav.api;

import ma.glasnost.orika.MapperFacade;
import no.nav.appserivces.tpsf.domain.request.RsBestilling;
import no.nav.appserivces.tpsf.domain.request.RsDollyBestillingsRequest;
import no.nav.appserivces.tpsf.service.DollyTpsfService;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.jpa.Bestilling;
import no.nav.resultSet.RsOpprettTestgruppe;
import no.nav.resultSet.RsTestgruppe;
import no.nav.resultSet.RsTestgruppeMedErMedlemOgFavoritt;
import no.nav.resultSet.RsTestident;
import no.nav.service.BestillingService;
import no.nav.service.IdentService;
import no.nav.service.TestgruppeService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static no.nav.util.UtilFunctions.isNullOrEmpty;

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
	DollyTpsfService dollyTpsfService;

	@Autowired
	private BestillingService bestillingService;

	@Autowired
	BestillingRepository bestillingRepository;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	public RsTestgruppeMedErMedlemOgFavoritt opprettTestgruppe(@RequestBody RsOpprettTestgruppe createTestgruppeRequest) {
		RsTestgruppe gruppe = testgruppeService.opprettTestgruppe(createTestgruppeRequest);
		return testgruppeService.rsTestgruppeToRsTestgruppeMedMedlemOgFavoritt(gruppe);
	}

	@PutMapping(value = "/{gruppeId}")
    public RsTestgruppeMedErMedlemOgFavoritt oppdaterTestgruppe(@PathVariable("gruppeId") Long gruppeId, @RequestBody RsOpprettTestgruppe testgruppe){
		RsTestgruppe gruppe = testgruppeService.oppdaterTestgruppe(gruppeId, testgruppe);
		return testgruppeService.rsTestgruppeToRsTestgruppeMedMedlemOgFavoritt(gruppe);
	}

	@PutMapping("/{gruppeId}/slettTestidenter")
	public void deleteTestident(@PathVariable("gruppeId") Long gruppeId, @RequestBody List<RsTestident> testpersonIdentListe) {
		identService.slettTestidenter(testpersonIdentListe);
	}

	@GetMapping("/{gruppeId}")
	public RsTestgruppeMedErMedlemOgFavoritt getTestgruppe(@PathVariable("gruppeId") Long gruppeId){
		RsTestgruppe gruppe = mapperFacade.map(testgruppeService.fetchTestgruppeById(gruppeId), RsTestgruppe.class);
		return testgruppeService.rsTestgruppeToRsTestgruppeMedMedlemOgFavoritt(gruppe);
	}

	@GetMapping
	public Set<RsTestgruppeMedErMedlemOgFavoritt> getTestgrupper(@RequestParam(name = "navIdent", required = false) String navIdent,
																@RequestParam(name = "teamId", required = false) Long teamId){
	    Set<RsTestgruppe> grupper;
		if(!isNullOrEmpty(navIdent)) {
			grupper = testgruppeService.fetchTestgrupperByTeammedlemskapAndFavoritterOfBruker(navIdent);
		} else {
			grupper = mapperFacade.mapAsSet(testgruppeService.fetchAlleTestgrupper(), RsTestgruppe.class);
		}

		if(!isNullOrEmpty(teamId)){
			grupper = grupper.stream().filter(gruppe -> gruppe.getTeam().getId().toString().equals(teamId.toString())).collect(Collectors.toSet());
		}

		if(!isNullOrEmpty(navIdent)){
			return testgruppeService.getRsTestgruppeMedErMedlem(grupper, navIdent);
		}

		return testgruppeService.getRsTestgruppeMedErMedlem(grupper);
	}

	@GetMapping("/{gruppeId}/attributter")
	public Set<String> getAttributterForGruppe(@PathVariable("gruppeId") String gruppeId){
		return null;
	}

	@GetMapping("/{gruppeId}/bestillingStatus")
	public Set<String> fetchBestillingsstatus(@PathVariable("testgruppeId") String gruppeId){
		return null;
	}

	@PostMapping("/{gruppeId}/bestilling")
	public RsBestilling opprettGruppe(@PathVariable("gruppeId") Long gruppeId, @RequestBody RsDollyBestillingsRequest request) {
		Bestilling bestilling = bestillingService.saveBestillingByGruppeIdAndAntallIdenter(gruppeId, request.getAntall(), request.getEnvironments());
		dollyTpsfService.opprettPersonerByKriterier(gruppeId, request, bestilling.getId());
		return mapperFacade.map(bestilling, RsBestilling.class);
	}
}
