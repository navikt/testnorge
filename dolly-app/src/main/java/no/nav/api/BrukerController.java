package no.nav.api;

import ma.glasnost.orika.MapperFacade;
import no.nav.api.resultSet.RsBruker;
import no.nav.jpa.Bruker;
import no.nav.service.BrukerService;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
	BrukerService brukerService;

	@Autowired
	MapperFacade mapperFacade;
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
	public void opprettBruker(@RequestBody RsBruker brukerRequest) {
		brukerService.opprettBruker(brukerRequest);
	}
	
	@GetMapping("/{navIdent}")
	public RsBruker getAllBrukerinfo(@PathVariable("navIdent") String navIdent) {
		Bruker bruker = brukerService.getBruker(navIdent);
		return mapperFacade.map(bruker, RsBruker.class);
	}

	@GetMapping
    public List<RsBruker> getAllBrukere(){
		return mapperFacade.mapAsList(brukerService.getBrukere(), RsBruker.class);
	}
//	TODO get team og grupper og testidenter for gitt medlemsskap og eierskap  (get all for gitt bruker) GetAllBrukerinfo
}
