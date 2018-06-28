package no.nav.api;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.repository.TeamRepository;
import no.nav.resultSet.RsBruker;
import no.nav.resultSet.RsOpprettTeam;
import no.nav.resultSet.RsTeam;
import no.nav.service.TeamService;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	public RsTeam opprettTeam(@RequestBody RsOpprettTeam createTeamRequest) {
		return teamService.opprettTeam(createTeamRequest);
	}

	@DeleteMapping("/{teamId}")
	public void deleteTeam(@PathVariable("teamId") Long teamId){
		teamService.deleteTeam(teamId);
	}

	@GetMapping("/{teamId}")
	public RsTeam fetchTeamById(@PathVariable("teamId") Long teamid){
		return mapperFacade.map(teamService.fetchTeamById(teamid), RsTeam.class);
	}

	@PutMapping("/{teamId}/leggTilMedlemmer")
	public RsTeam addBrukereSomTeamMedlemmer(@PathVariable("teamId") Long teamId, @RequestBody List<RsBruker> brukereRequest) {
		return teamService.addMedlemmer(teamId, brukereRequest);
	}
	
	@PutMapping("/{teamId}/fjernMedlemmer")
	public RsTeam fjernBrukerefraTeam(@PathVariable("teamId") Long teamId, @RequestBody List<RsBruker> brukereRequest) {
        return teamService.fjernMedlemmer(teamId, brukereRequest);
    }

    @PutMapping(path = "/update")
    public RsTeam endreTeaminfo(@RequestBody RsTeam createTeamRequest) {
        return teamService.updateTeamInfo(createTeamRequest);
    }

    @GetMapping("/bruker/{navIdent}")
    public List<RsTeam> getTeamByNavident(@PathVariable("navIdent") String navIdent){
		return mapperFacade.mapAsList(teamService.fetchTeamsByMedlemskapInTeams(navIdent), RsTeam.class);
	}

	@GetMapping
	public List<RsTeam> getTeams(){
		return mapperFacade.mapAsList(teamRepository.findAll(), RsTeam.class);
	}
	
}
