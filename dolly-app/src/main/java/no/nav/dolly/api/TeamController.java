package no.nav.dolly.api;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.repository.TeamRepository;
import no.nav.dolly.domain.resultset.RsOpprettTeam;
import no.nav.dolly.domain.resultset.RsTeam;
import no.nav.dolly.service.TeamService;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1/team")
public class TeamController {

	@Autowired
	private TeamService teamService;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private MapperFacade mapperFacade;

    @GetMapping
    public List<RsTeam> getTeams(@RequestParam(name="navIdent", required = false) String navIdent){
        if(navIdent != null && !navIdent.isEmpty()) {
            return mapperFacade.mapAsList(teamService.fetchTeamsByMedlemskapInTeams(navIdent), RsTeam.class);
        }
        return mapperFacade.mapAsList(teamRepository.findAll(), RsTeam.class);
    }

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
	public RsTeam addBrukereSomTeamMedlemmerByNavidenter(@PathVariable("teamId") Long teamId, @RequestBody List<String> navIdenter) {
		return teamService.addMedlemmerByNavidenter(teamId, navIdenter);
	}

	@PutMapping("/{teamId}/fjernMedlemmer")
	public RsTeam fjernBrukerefraTeam(@PathVariable("teamId") Long teamId, @RequestBody List<String> navIdenter) {
        return teamService.fjernMedlemmer(teamId, navIdenter);
    }

    @PutMapping("/{teamId}")
    public RsTeam endreTeaminfo(@PathVariable("teamId") Long teamId , @RequestBody RsTeam createTeamRequest) {
        return teamService.updateTeamInfo(teamId, createTeamRequest);
    }
}
