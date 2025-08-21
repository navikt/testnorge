package no.nav.dolly.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.entity.team.RsTeam;
import no.nav.dolly.domain.resultset.entity.team.RsTeamUpdate;
import no.nav.dolly.domain.resultset.entity.team.RsTeamWithBrukere;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.repository.BrukerRepository;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static no.nav.dolly.config.CachingConfig.CACHE_BRUKER;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/team")
public class TeamController {

    private final TeamService teamService;
    private final MapperFacade mapperFacade;
    private final BrukerRepository brukerRepository;

    @GetMapping
    @Operation(description = "Hent alle team")
    public Flux<RsTeamWithBrukere> getAllTeams() {

        return teamService.fetchAllTeam()
                .flatMap(this::mapTeam);
    }

    @GetMapping("/{id}")
    @Operation(description = "Hent team med angitt ID")
    public Mono<RsTeamWithBrukere> getTeamById(@PathVariable("id") Long id) {

        return teamService.fetchTeamById(id)
                .flatMap(this::mapTeam);
    }

    @PostMapping
    @Operation(description = "Opprett nytt team")
    @CacheEvict(value = {CACHE_BRUKER}, allEntries = true)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RsTeamWithBrukere> createTeam(@RequestBody RsTeam rsTeam) {

        return teamService.opprettTeam(rsTeam)
                .flatMap(this::mapTeam);
    }

    @PutMapping("/{id}")
    @CacheEvict(value = {CACHE_BRUKER}, allEntries = true)
    @Operation(description = "Oppdater eksisterende team")
    public Mono<RsTeamWithBrukere> updateTeam(@PathVariable("id") Long id, @RequestBody RsTeamUpdate rsTeam) {

        return teamService.updateTeam(id, rsTeam)
                .flatMap(this::mapTeam);
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = {CACHE_BRUKER}, allEntries = true)
    @Operation(description = "Slett team")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteTeam(@PathVariable("id") Long id) {

        return teamService.deleteTeamById(id);
    }

    @PostMapping("/{teamId}/medlem/{brukerId}")
    @CacheEvict(value = {CACHE_BRUKER}, allEntries = true)
    @Operation(description = "Legg til bruker i team")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> addTeamMember(@PathVariable("teamId") Long teamId,
                              @PathVariable("brukerId") String brukerId) {

        return teamService.addBrukerToTeam(teamId, brukerId);
    }

    @DeleteMapping("/{teamId}/medlem/{brukerId}")
    @CacheEvict(value = {CACHE_BRUKER}, allEntries = true)
    @Operation(description = "Fjern bruker fra team")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> removeTeamMember(@PathVariable("teamId") Long teamId,
                                       @PathVariable("brukerId") String brukerId) {

        return teamService.removeBrukerFromTeam(teamId, brukerId);
    }

    private Mono<RsTeamWithBrukere> mapTeam(Team team) {

        return brukerRepository.findById(team.getBrukerId())
                .zipWith(Mono.just(team))
                .map(tuple -> {
                    var context = MappingContextUtils.getMappingContext();
                    context.setProperty("brukerId", tuple.getT1().getBrukerId());
                    return mapperFacade.map(tuple.getT2(), RsTeamWithBrukere.class, context);
                });
    }
}