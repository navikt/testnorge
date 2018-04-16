package no.nav.api;

import no.nav.service.TestgroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/testgruppe")
public class TestgroupController {
	
	@Autowired
	TestgroupService testgroupService;
	
	@PostMapping(value = "/{testgruppeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void persisterTestidenter(@PathVariable("testgruppeId") Long gruppeId, @RequestBody List<Long> personIdentListe) {
		testgroupService.persisterTestidenter(gruppeId, personIdentListe);
	}
	
	
}
