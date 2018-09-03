package no.nav.dolly.api;

import ma.glasnost.orika.MapperFacade;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultSet.RsBruker;
import no.nav.dolly.domain.resultSet.RsBrukerTeamAndGruppeIDs;
import no.nav.dolly.service.BrukerService;
import no.nav.dolly.service.TeamService;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/bruker", produces = MediaType.APPLICATION_JSON_VALUE)
public class BrukerController {
	
	@Autowired
	private BrukerService brukerService;

	@Autowired
	private MapperFacade mapperFacade;

	@GetMapping("/{navIdent}")
	public RsBrukerTeamAndGruppeIDs getBrukerByNavIdent(@PathVariable("navIdent") String navIdent) {
		Bruker bruker = brukerService.fetchBruker(navIdent);
		return mapperFacade.map(bruker, RsBrukerTeamAndGruppeIDs.class);
	}

	@GetMapping("/current")
	public RsBruker getCurrentBruker() {
		OidcTokenAuthentication auth = (OidcTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
		Bruker bruker = brukerService.fetchOrCreateBruker(auth.getPrincipal());
		return mapperFacade.map(bruker, RsBruker.class);
	}

	@GetMapping
    public List<RsBrukerTeamAndGruppeIDs> getAllBrukere(){
		return mapperFacade.mapAsList(brukerService.fetchBrukere(), RsBrukerTeamAndGruppeIDs.class);
	}

}
