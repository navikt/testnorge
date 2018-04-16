package no.nav.api;

import no.nav.api.request.CreateTeamRequest;
import no.nav.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1/team", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class TeamController {

	@Autowired
	TeamService teamService;

	@PostMapping
	public void opprettTeam(@RequestBody CreateTeamRequest createTeamRequest) {
		teamService.opprettTeam(createTeamRequest);
	}
	
	//legg til brukere i team
	//fjern bruker-medlemmer fra team
	//endre teaminfo
	//opprette gruppe
	//endre eier av team
	
	// slett team
	
	
}
