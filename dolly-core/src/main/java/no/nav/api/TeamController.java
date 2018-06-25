package no.nav.api;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.repository.TeamRepository;
import no.nav.jpa.Team;
import no.nav.resultSet.RsBruker;
import no.nav.resultSet.RsTeam;
import no.nav.service.TeamService;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1/team")
public class TeamController {

	@Autowired
	private TeamService teamService;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private MapperFacade mapperFacade;

	@PostMapping
	public RsTeam opprettTeam(@RequestBody RsTeam createTeamRequest) {
		return teamService.opprettTeam(createTeamRequest);
	}

	@DeleteMapping("/{teamId}")
	public void deleteTeam(@PathVariable("teamId") Long teamId){
		teamRepository.deleteById(teamId);
	}

	@PutMapping("/{teamId}/leggTilMedlemmer")
	public void addBrukereSomTeamMedlemmer(@PathVariable("teamId") Long teamId, @RequestBody List<RsBruker> brukereRequest) {
		teamService.addMedlemmer(teamId, brukereRequest);
	}
	
	@PutMapping("/{teamId}/fjernMedlemmer")
	public RsTeam fjernBrukerefraTeam(@PathVariable("teamId") Long teamId, @RequestBody List<RsBruker> brukereRequest) {
        Team savedTeam = teamService.fjernMedlemmer(teamId, brukereRequest);
        return mapperFacade.map(savedTeam, RsTeam.class);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public RsTeam endreTeaminfo(@RequestBody RsTeam createTeamRequest) {
        Team savedTeam = teamService.updateTeamInfo(createTeamRequest);
		return mapperFacade.map(savedTeam, RsTeam.class);
    }

    @GetMapping("/bruker/{navIdent}")
    public List<RsTeam> getTeamByNavident(@PathVariable("navIdent") String navIdent){
		List<Team> teams = teamService.fetchTeamsByMedlemskapInTeams(navIdent);
		return mapperFacade.mapAsList(teams, RsTeam.class);
	}

    @ResponseStatus(HttpStatus.OK)
	@GetMapping
	public List<RsTeam> getTeams(){
		List<Team> teams = teamRepository.findAll();
		return mapperFacade.mapAsList(teams, RsTeam.class);
	}
	
}
