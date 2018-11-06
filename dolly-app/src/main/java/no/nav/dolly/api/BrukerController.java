package no.nav.dolly.api;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.resultset.RsBrukerUpdateFavoritterReq;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.RsBruker;
import no.nav.dolly.domain.resultset.RsBrukerTeamAndGruppeIDs;
import no.nav.dolly.service.BrukerService;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
		Bruker bruker = brukerService.fetchOrCreateBruker(auth.getPrincipal().toUpperCase());
		return mapperFacade.map(bruker, RsBruker.class);
	}

	@GetMapping
    public List<RsBrukerTeamAndGruppeIDs> getAllBrukere(){
		return mapperFacade.mapAsList(brukerService.fetchBrukere(), RsBrukerTeamAndGruppeIDs.class);
	}

	@PutMapping("/leggTilFavoritt")
	public RsBruker leggTilFavoritt(@RequestBody RsBrukerUpdateFavoritterReq request){
		return mapperFacade.map(brukerService.leggTilFavoritter(Arrays.asList(request.getGruppeId())), RsBruker.class);
	}

	@PutMapping("/fjernFavoritt")
	public RsBruker fjernFavoritt(@RequestBody RsBrukerUpdateFavoritterReq request){
		return mapperFacade.map(brukerService.fjernFavoritter(Arrays.asList(request.getGruppeId())), RsBruker.class);
	}

}
