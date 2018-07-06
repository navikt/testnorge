package no.nav.api;

import ma.glasnost.orika.MapperFacade;
import no.nav.resultSet.RsOpprettTestgruppe;
import no.nav.resultSet.RsTestgruppe;
import no.nav.resultSet.RsTestgruppeMedErMedlemOgFavoritt;
import no.nav.resultSet.RsTestident;
import no.nav.service.IdentService;
import no.nav.service.TestgruppeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

@RestController
@RequestMapping(value = "api/v1/testgruppe")
public class TestgruppeController {
	
	@Autowired
	private TestgruppeService testgruppeService;

	@Autowired
	private IdentService identService;

	@Autowired
	private MapperFacade mapperFacade;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	public RsTestgruppe opprettTestgruppe(@RequestBody RsOpprettTestgruppe createTestgruppeRequest) {
	    return testgruppeService.opprettTestgruppe(createTestgruppeRequest);
	}

	//@ResponseStatus(HttpStatus.CREATED)
	//@PostMapping(value = "/{testgruppeId}")
	//public void persisterTestidenter(@PathVariable("testgruppeId") Long gruppeId, @RequestBody List<RsTestident> testpersonIdentListe) {
	//	testgruppeService.saveAllIdenterToTestgruppe(gruppeId, testpersonIdentListe);
	//}

	@PutMapping(value = "/{gruppeId}")
    public RsTestgruppeMedErMedlemOgFavoritt oppdaterTestgruppe(@PathVariable("gruppeId") Long gruppeId, @RequestBody RsTestgruppe testgruppe){
		RsTestgruppe testgruppeRes = testgruppeService.oppdaterTestgruppe(gruppeId, testgruppe);
		return new ArrayList<>(testgruppeService.getRsTestgruppeMedErMedlem(new HashSet<>(Arrays.asList(testgruppeRes)))).get(0);
	}

	@PutMapping("/{gruppe}/slettTestidenter")
	public void deleteTestident(@PathVariable("gruppe") Long gruppeId, @RequestBody List<RsTestident> testpersonIdentListe) {
		identService.slettTestidenter(testpersonIdentListe);
	}

	@GetMapping("/{gruppeId}")
	public RsTestgruppeMedErMedlemOgFavoritt getTestgruppe(@PathVariable("gruppeId") Long gruppeId){
		RsTestgruppe testgruppe = mapperFacade.map(testgruppeService.fetchTestgruppeById(gruppeId), RsTestgruppe.class);
		return new ArrayList<>(testgruppeService.getRsTestgruppeMedErMedlem(new HashSet<>(Arrays.asList(testgruppe)))).get(0);
	}

	@GetMapping
	public Set<RsTestgruppeMedErMedlemOgFavoritt> getTestgrupper(@RequestParam(name = "navIdent", required = false) String navIdent){
	    Set<RsTestgruppe> grupper;
		if(navIdent != null && !navIdent.isEmpty()) {
			grupper = testgruppeService.fetchTestgrupperByTeammedlemskapAndFavoritterOfBruker(navIdent);
			return testgruppeService.getRsTestgruppeMedErMedlem(grupper, navIdent);
		}

		grupper = mapperFacade.mapAsSet(testgruppeService.fetchAlleTestgrupper(), RsTestgruppe.class);
		return testgruppeService.getRsTestgruppeMedErMedlem(grupper);
	}

	//@GetMapping("/bruker/{navident}")
	//public Set<RsTestgruppe> getTestgrupperForBruker(@PathVariable("navident") String navident){
	//	return testgruppeService.fetchTestgrupperByTeammedlemskapAndFavoritterOfBruker(navident);
	//}

	@GetMapping("/{gruppeId}/attributter")
	public Set<String> getAttributterForGruppe(@PathVariable("gruppeId") String gruppeId){
		return null;
	}

	@GetMapping("/{gruppeId}/bestillingStatus")
	public Set<String> fetchBestillingsstatus(@PathVariable("testgruppeId") String gruppeId){
		return null;
	}

	@PostMapping("/{gruppeId}/bestilling")
	public Set<String> createBestilling(@PathVariable("testgruppeId") String gruppeId, @RequestBody Object bestilling){
		return null;
	}
}
