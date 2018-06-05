package no.nav.api;

import ma.glasnost.orika.MapperFacade;
import no.nav.api.resultSet.RsBruker;
import no.nav.api.resultSet.RsTeam;
import no.nav.jpa.Team;
import no.nav.repository.TeamRepository;
import no.nav.service.TeamService;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1/team", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class TeamController {

	@Autowired
	TeamService teamService;

	@Autowired
	TeamRepository teamRepository;

	@Autowired
	private MapperFacade mapperFacade;

	@PostMapping
	public RsTeam opprettTeam(@RequestBody RsTeam createTeamRequest) {
		Team savedTeam = teamService.opprettTeam(createTeamRequest);
		return mapperFacade.map(savedTeam, RsTeam.class);
	}
	
	@PutMapping("/{team_id}/leggTilMedlemmer")
	public void addBrukereSomTeamMedlemmer(@PathVariable("team_id") Long teamId, @RequestBody List<RsBruker> brukereRequest) {
		teamService.addMedlemmer(teamId, brukereRequest);
	}
	
	@PutMapping("/{team_id}/fjernMedlemmer")
	public RsTeam fjernBrukerefraTeam(@PathVariable("team_id") Long teamId, @RequestBody List<RsBruker> brukereRequest) {
        Team savedTeam = teamService.fjernMedlemmer(teamId, brukereRequest);
        return mapperFacade.map(savedTeam, RsTeam.class);
    }
		
    @PutMapping("/{team_id}")
    public RsTeam endreTeaminfo(@PathVariable("team_id") Long teamId, @RequestBody RsTeam createTeamRequest) {
        Team savedTeam = teamService.updateTeamInfo(teamId, createTeamRequest);
		return mapperFacade.map(savedTeam, RsTeam.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
	public List<RsTeam> getTeams(){
		List<Team> teams = teamRepository.findAll();
		return mapperFacade.mapAsList(teams, RsTeam.class);
	}
	
	// slett team
 
}
