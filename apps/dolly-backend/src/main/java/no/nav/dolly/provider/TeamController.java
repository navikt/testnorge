package no.nav.dolly.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.entity.team.RsTeam;
import no.nav.dolly.domain.resultset.entity.team.RsTeamWithBrukere;
import no.nav.dolly.service.TeamService;
import org.springframework.cache.annotation.CacheEvict;
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

import java.util.List;

import static no.nav.dolly.config.CachingConfig.CACHE_BRUKER;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/team")
public class TeamController {

    private final TeamService teamService;
    private final MapperFacade mapperFacade;

    @GetMapping
    @Operation(description = "Hent alle team")
    public List<RsTeamWithBrukere> getAllTeams() {
        var teams = teamService.fetchAllTeam();
        return mapperFacade.mapAsList(teams, RsTeamWithBrukere.class);
    }

    @GetMapping("/{id}")
    @Operation(description = "Hent team med angitt ID")
    public RsTeamWithBrukere getTeamById(@PathVariable("id") Long id) {
        var team = teamService.fetchTeamById(id);
        return mapperFacade.map(team, RsTeamWithBrukere.class);
    }

    @PostMapping
    @Operation(description = "Opprett nytt team")
    @CacheEvict(value = { CACHE_BRUKER }, allEntries = true)
    @ResponseStatus(HttpStatus.CREATED)
    public RsTeamWithBrukere createTeam(@RequestBody RsTeam rsTeam) {
        var team = mapperFacade.map(rsTeam, Team.class);
        var savedTeam = teamService.opprettTeam(team);
        return mapperFacade.map(savedTeam, RsTeamWithBrukere.class);
    }

    @PutMapping("/{id}")
    @CacheEvict(value = { CACHE_BRUKER }, allEntries = true)
    @Operation(description = "Oppdater eksisterende team")
    public RsTeamWithBrukere updateTeam(@PathVariable("id") Long id, @RequestBody RsTeam rsTeam) {
        var team = mapperFacade.map(rsTeam, Team.class);
        var updatedTeam = teamService.updateTeam(id, team);
        return mapperFacade.map(updatedTeam, RsTeamWithBrukere.class);
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = { CACHE_BRUKER }, allEntries = true)
    @Operation(description = "Slett team")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void deleteTeam(@PathVariable("id") Long id) {
        teamService.deleteTeamById(id);
    }

    @PostMapping("/{teamId}/medlem/{brukerId}")
    @CacheEvict(value = { CACHE_BRUKER }, allEntries = true)
    @Operation(description = "Legg til bruker i team")
    @ResponseStatus(HttpStatus.CREATED)
    public void addTeamMember(@PathVariable("teamId") Long teamId,
                              @PathVariable("brukerId") String brukerId) {
        teamService.addBrukerToTeam(teamId, brukerId);
    }

    @DeleteMapping("/{teamId}/medlem/{brukerId}")
    @CacheEvict(value = { CACHE_BRUKER }, allEntries = true)
    @Operation(description = "Fjern bruker fra team")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeTeamMember(@PathVariable("teamId") Long teamId,
                                 @PathVariable("brukerId") String brukerId) {
        teamService.removeBrukerFromTeam(teamId, brukerId);
    }
}