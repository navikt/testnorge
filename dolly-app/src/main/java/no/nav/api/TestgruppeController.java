package no.nav.api;

import ma.glasnost.orika.MapperFacade;
import no.nav.api.resultSet.RsTestgruppe;
import no.nav.api.resultSet.RsTestident;
import no.nav.jpa.Testgruppe;
import no.nav.service.IdentService;
import no.nav.service.TestgruppeService;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1/testgruppe", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class TestgruppeController {
	
	@Autowired
	TestgruppeService testgruppeService;

	@Autowired
	IdentService identService;

	@Autowired
	MapperFacade mapperFacade;
	
	@PostMapping(value = "/team/{teamId}")
	public RsTestgruppe opprettTestgruppe(@PathVariable Long teamId, @RequestBody RsTestgruppe createTestgruppeRequest) {
		Testgruppe testgruppe = testgruppeService.opprettTestgruppe(teamId, createTestgruppeRequest);
        return mapperFacade.map(testgruppe, RsTestgruppe.class);
	}
	
	@PostMapping(value = "/{testgruppeId}")
	public void persisterTestidenter(@PathVariable("testgruppeId") Long gruppeId, @RequestBody List<RsTestident> testpersonIdentListe) {
		identService.persisterTestidenter(gruppeId, testpersonIdentListe);
	}
	
	//TODO REST endre navn gruppe
	
	//TODO Slett gruppe
	
	@PutMapping("/{testgruppe}/slettTestidenter")
	public void deleteTestident(@PathVariable("testgruppe") Long gruppeId, @RequestBody List<RsTestident> testpersonIdentListe) {
		identService.slettTestidenter(testpersonIdentListe);
	}

	@GetMapping("/{gruppeId}")
	public RsTestgruppe getTestgruppe(@PathVariable("gruppeId") Long gruppeId){
		return mapperFacade.map(testgruppeService.fetchTestgruppeById(gruppeId), RsTestgruppe.class);
	}

	@GetMapping
	public List<RsTestgruppe> getTestgrupper(){
		return mapperFacade.mapAsList(testgruppeService.fetchAlleTestgrupper(), RsTestgruppe.class);
	}
}
