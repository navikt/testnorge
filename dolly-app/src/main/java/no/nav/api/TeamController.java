package no.nav.api;

import no.nav.api.request.CreateTeamRequest;
import no.nav.api.response.TeamResponse;
import no.nav.jpa.Team;
import no.nav.mapper.MapTeamToResponse;
import no.nav.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1/team", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class TeamController {

	@Autowired
	TeamService teamService;
	@Autowired
	private MapTeamToResponse mapTeamToResponse;
	@PostMapping
	public @ResponseBody
	TeamResponse opprettTeam(@RequestBody CreateTeamRequest createTeamRequest) {
		Team savedTeam = teamService.opprettTeam(createTeamRequest);
		return mapTeamToResponse.map(savedTeam);
	}
	
	//legg til brukere i team
	//fjern bruker-medlemmer fra team
	//endre teaminfo /oppdater teamMetadata
	//opprette gruppe
	//endre eier av team
	
	// slett team
	
	
}
