package no.nav.api;

import no.nav.api.request.BrukerRequest;
import no.nav.service.BrukerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/bruker", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
	
	@Autowired
	BrukerService brukerService;
	
	@PostMapping
	public void opprettBruker(@RequestBody() BrukerRequest brukerRequest) {
		brukerService.opprettBruker(brukerRequest.getNavIdent());
	}
	
//	TODO get team og grupper og testidenter for gitt medlemsskap og eierskap  (get all for gitt bruker) GetAllBrukerinfo
}
