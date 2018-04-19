package no.nav.api;

import no.nav.api.request.CreateTestgruppeRequest;
import no.nav.api.request.IdentListeRequest;
import no.nav.api.response.TestgruppeResponse;
import no.nav.jpa.Testgruppe;
import no.nav.mapper.MapTestgruppeToResponse;
import no.nav.service.IdentService;
import no.nav.service.TestgroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/testgruppe", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class TestgroupController {
	
	@Autowired
	TestgroupService testgroupService;
	
	@Autowired
	IdentService identService;
	
	@PostMapping
	public TestgruppeResponse opprettTestgruppe(@RequestBody CreateTestgruppeRequest createTestgruppeRequest) {
		Testgruppe testgruppe = testgroupService.opprettTestgruppe(createTestgruppeRequest);
		return MapTestgruppeToResponse.map(testgruppe);
	}
	
	@PostMapping(value = "/{testgruppeId}")
	public void persisterTestidenter(@PathVariable("testgruppeId") Long gruppeId, @RequestBody IdentListeRequest testpersonIdentListe) {
		identService.persisterTestidenter(gruppeId, testpersonIdentListe.getIdentListe());
	}
	
	//TODO REST endre gruppe
	
	//TODO Slett gruppe
	
	@PutMapping("/{testgruppe}/slettTestidenter")
	public ResponseEntity deleteTestident(@PathVariable("testgruppe") Long gruppeId, @RequestBody IdentListeRequest testpersonIdentListe) {
		identService.slettTestidenter( testpersonIdentListe.getIdentListe());
		return ResponseEntity.ok(HttpEntity.EMPTY);
	}
}
