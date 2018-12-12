package no.nav.dolly.api;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.resultset.RsOpprettTeam;
import no.nav.dolly.domain.resultset.RsTeam;
import no.nav.dolly.domain.resultset.RsTeamUtvidet;
import no.nav.dolly.repository.TeamRepository;
import no.nav.dolly.service.TeamService;

@Transactional
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
    public List<RsTeam> getTeams(@RequestParam("navIdent") Optional<String> navIdent){
        return navIdent
				.map(navId -> mapperFacade.mapAsList(teamService.fetchTeamsByMedlemskapInTeams(navId), RsTeam.class))
				.orElse(mapperFacade.mapAsList(teamRepository.findAll(), RsTeam.class));
    }

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	public RsTeamUtvidet opprettTeam(@RequestBody RsOpprettTeam createTeamRequest) {
		return teamService.opprettTeam(createTeamRequest);
	}

	@DeleteMapping("/{teamId}")
	public void deleteTeam(@PathVariable("teamId") Long teamId){
		teamService.deleteTeam(teamId);
	}

	@GetMapping("/{teamId}")
	public RsTeamUtvidet fetchTeamById(@PathVariable("teamId") Long teamid){
		return mapperFacade.map(teamService.fetchTeamById(teamid), RsTeamUtvidet.class);
	}

	@PutMapping("/{teamId}/leggTilMedlemmer")
	public RsTeamUtvidet addBrukereSomTeamMedlemmerByNavidenter(@PathVariable("teamId") Long teamId, @RequestBody List<String> navIdenter) {
		return teamService.addMedlemmerByNavidenter(teamId, navIdenter);
	}

	@PutMapping("/{teamId}/fjernMedlemmer")
	public RsTeamUtvidet fjernBrukerefraTeam(@PathVariable("teamId") Long teamId, @RequestBody List<String> navIdenter) {
        return teamService.fjernMedlemmer(teamId, navIdenter);
    }

    @PutMapping("/{teamId}")
    public RsTeamUtvidet endreTeaminfo(@PathVariable("teamId") Long teamId , @RequestBody RsTeamUtvidet createTeamRequest) {
        return teamService.updateTeamInfo(teamId, createTeamRequest);
    }
}
