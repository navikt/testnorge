package no.nav.api;

import no.nav.api.request.BrukerRequest;
import no.nav.api.response.BrukerResponse;
import no.nav.jpa.Bruker;
import no.nav.mapper.MapBrukerToResponse;
import no.nav.service.BrukerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/bruker", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
	
	@Autowired
	BrukerService brukerService;
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public void opprettBruker(@RequestBody() BrukerRequest brukerRequest) {
		brukerService.opprettBruker(brukerRequest.getNavIdent());
	}
	
	@GetMapping("/{navIdent}")
	public BrukerResponse getAllBrukerinfo(@PathVariable("navIdent") String navIdent) {
		Bruker bruker =brukerService.getBruker(navIdent);
		return MapBrukerToResponse.map(bruker);
	}
//	TODO get team og grupper og testidenter for gitt medlemsskap og eierskap  (get all for gitt bruker) GetAllBrukerinfo
}
