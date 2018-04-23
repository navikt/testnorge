package no.nav.api;

import no.nav.api.request.BrukereRequest;
import no.nav.api.request.CreateTeamRequest;
import no.nav.api.response.TeamResponse;
import no.nav.jpa.Team;
import no.nav.mapper.MapTeamToResponse;
import no.nav.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
	public @ResponseBody TeamResponse opprettTeam(@RequestBody CreateTeamRequest createTeamRequest) {
		Team savedTeam = teamService.opprettTeam(createTeamRequest);
		return mapTeamToResponse.map(savedTeam);
	}
	
	@PutMapping("/{team_id}/leggTilMedlemmer")
	public void addBrukereSomTeamMedlemmer(@PathVariable("team_id") Long teamId, @RequestBody BrukereRequest brukereRequest) {
		teamService.addMedlemmer(teamId, brukereRequest.getNavIdenter());
	}
	
	@PutMapping("/{team_id}/fjernMedlemmer")
	public @ResponseBody TeamResponse fjernBrukerefraTeam(@PathVariable("team_id") Long teamId, @RequestBody BrukereRequest brukereRequest) {
        Team savedTeam = teamService.fjernMedlemmer(teamId, brukereRequest.getNavIdenter());
        return mapTeamToResponse.map(savedTeam);
    }
		
    @PutMapping("/{team_id}")
    public @ResponseBody TeamResponse endreTeaminfo(@PathVariable("team_id") Long teamId, @RequestBody CreateTeamRequest createTeamRequest) {
        Team savedTeam = teamService.updateTeamInfo(teamId, createTeamRequest);
        return mapTeamToResponse.map(savedTeam);
    }
	
	// slett team
 
}
