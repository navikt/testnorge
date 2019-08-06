package no.nav.dolly.provider.api;

import static java.lang.String.format;
import static no.nav.dolly.config.CachingConfig.CACHE_TEAM;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.resultset.RsOpprettTeam;
import no.nav.dolly.domain.resultset.RsTeam;
import no.nav.dolly.domain.resultset.RsTeamUtvidet;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.service.TeamService;
import no.nav.dolly.service.TestgruppeService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

import java.util.List;
import java.util.Optional;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v1/team")
public class TeamController {

    private final TeamService teamService;
    private final TestgruppeService testgruppeService;

    @Cacheable(CACHE_TEAM)
    @GetMapping
    public List<RsTeam> getTeams(@RequestParam(value = "navIdent", required = false) String navIdent) {
        return Optional.ofNullable(navIdent)
                .map(teamService::fetchTeamsByMedlemskapInTeamsMapped)
                .orElse(teamService.findAllOrderByNavn());
    }

    @CacheEvict(value = CACHE_TEAM, allEntries = true)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public RsTeamUtvidet opprettTeam(@RequestBody RsOpprettTeam createTeamRequest) {
        return teamService.opprettTeam(createTeamRequest);
    }

    @CacheEvict(value = CACHE_TEAM, allEntries = true)
    @DeleteMapping("/{teamId}")
    public void deleteTeam(@PathVariable("teamId") Long teamId) {
        if (teamService.deleteTeam(teamId) == 0) {
            throw new NotFoundException(format("Team med id %d ble ikke funnet.", teamId));
        }
        //TODO Verifiser i ende-til-ende test
        testgruppeService.slettGruppeByTeamId(teamId);
    }

    @Cacheable(CACHE_TEAM)
    @GetMapping("/{teamId}")
    public RsTeamUtvidet fetchTeamById(@PathVariable("teamId") Long teamid) {
        return teamService.getTeamById(teamid);
    }

    @CacheEvict(value = CACHE_TEAM, allEntries = true)
    @PutMapping("/{teamId}/leggTilMedlemmer")
    public RsTeamUtvidet addBrukereSomTeamMedlemmerByNavidenter(@PathVariable("teamId") Long teamId, @RequestBody List<String> navIdenter) {
        return teamService.addMedlemmerByNavidenter(teamId, navIdenter);
    }

    @CacheEvict(value = CACHE_TEAM, allEntries = true)
    @PutMapping("/{teamId}/fjernMedlemmer")
    public RsTeamUtvidet fjernBrukerefraTeam(@PathVariable("teamId") Long teamId, @RequestBody List<String> navIdenter) {
        return teamService.fjernMedlemmer(teamId, navIdenter);
    }

    @CacheEvict(value = CACHE_TEAM, allEntries = true)
    @DeleteMapping("/{teamId}/deleteMedlem")
    public RsTeamUtvidet deleteMedlemfraTeam(@PathVariable("teamId") Long teamId, @RequestParam String navIdent) {
        return teamService.slettMedlem(teamId, navIdent);
    }

    @CacheEvict(value = CACHE_TEAM, allEntries = true)
    @PutMapping("/{teamId}")
    public RsTeamUtvidet endreTeaminfo(@PathVariable("teamId") Long teamId, @RequestBody RsTeamUtvidet createTeamRequest) {
        return teamService.updateTeamInfo(teamId, createTeamRequest);
    }
}
