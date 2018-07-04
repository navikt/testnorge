package no.nav.api;

import ma.glasnost.orika.MapperFacade;
import no.nav.jpa.Testgruppe;
import no.nav.resultSet.RsOpprettTestgruppe;
import no.nav.resultSet.RsTestgruppe;
import no.nav.resultSet.RsTestident;
import no.nav.service.IdentService;
import no.nav.service.TestgruppeService;

import java.util.List;
import java.util.Optional;
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

	@PutMapping(value = "/{testgruppeId}")
    public RsTestgruppe oppdaterTestgruppe(@PathVariable("testgruppeId") Long gruppeId, @RequestBody RsTestgruppe testgruppe){
		return testgruppeService.oppdaterTestgruppe(gruppeId, testgruppe);
	}

	@PutMapping("/{testgruppe}/slettTestidenter")
	public void deleteTestident(@PathVariable("testgruppe") Long gruppeId, @RequestBody List<RsTestident> testpersonIdentListe) {
		identService.slettTestidenter(testpersonIdentListe);
	}

	@GetMapping("/{testgruppeId}")
	public RsTestgruppe getTestgruppe(@PathVariable("testgruppeId") Long gruppeId){
		return mapperFacade.map(testgruppeService.fetchTestgruppeById(gruppeId), RsTestgruppe.class);
	}

	@GetMapping
	public Set<RsTestgruppe> getTestgrupper(@RequestParam("team") Optional<String> teamnavn, @RequestParam(name = "navIdent", required = false) String navIdent){
		if(navIdent != null && !navIdent.isEmpty()) {
			return testgruppeService.fetchTestgrupperByTeammedlemskapAndFavoritterOfBruker(navIdent);
		}
		return mapperFacade.mapAsSet(testgruppeService.fetchAlleTestgrupper(), RsTestgruppe.class);
	}

	//@GetMapping("/bruker/{navident}")
	//public Set<RsTestgruppe> getTestgrupperForBruker(@PathVariable("navident") String navident){
	//	return testgruppeService.fetchTestgrupperByTeammedlemskapAndFavoritterOfBruker(navident);
	//}

	@GetMapping("/{testgruppeId}/attributter")
	public Set<String> getAttributterForGruppe(@PathVariable("testgruppeId") String gruppeId){
		return null;
	}
}
