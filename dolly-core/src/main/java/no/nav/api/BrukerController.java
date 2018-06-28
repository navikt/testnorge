package no.nav.api;

import ma.glasnost.orika.MapperFacade;
import no.nav.freg.security.oidc.common.OidcTokenAuthentication;
import no.nav.jpa.Bruker;
import no.nav.resultSet.RsBruker;
import no.nav.service.BrukerService;
import no.nav.service.TeamService;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/bruker", produces = MediaType.APPLICATION_JSON_VALUE)
public class BrukerController {
	
	@Autowired
	private BrukerService brukerService;

	@Autowired
	private TeamService teamService;

	@Autowired
	private MapperFacade mapperFacade;
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
	public void opprettBruker(@RequestBody RsBruker brukerRequest) {
		brukerService.opprettBruker(brukerRequest);
	}

	@GetMapping("/{navIdent}")
	public RsBruker getBrukerByNavIdent(@PathVariable("navIdent") String navIdent) {
		Bruker bruker = brukerService.fetchBruker(navIdent);
		return mapperFacade.map(bruker, RsBruker.class);
	}

	@GetMapping("/current")
	public RsBruker getCurrentBruker() {
		OidcTokenAuthentication auth = (OidcTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
		Bruker bruker = brukerService.fetchBruker(auth.getPrincipal());
		return mapperFacade.map(bruker, RsBruker.class);
	}

	@GetMapping
    public List<RsBruker> getAllBrukere(){
		return mapperFacade.mapAsList(brukerService.getBrukere(), RsBruker.class);
	}

}
