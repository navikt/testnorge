package no.nav.dolly.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.BrukerFavoritter;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.TeamBruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUpdateFavoritterReq;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUtenFavoritter;
import no.nav.dolly.domain.resultset.entity.team.RsTeamWithBrukere;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.repository.BrukerFavoritterRepository;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TeamBrukerRepository;
import no.nav.dolly.repository.TeamRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.service.BrukerService;
import no.nav.dolly.service.TeamService;
import no.nav.testnav.libs.reactivesecurity.action.GetUserInfo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.HashMap;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static no.nav.dolly.config.CachingConfig.CACHE_BRUKER;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/bruker", produces = MediaType.APPLICATION_JSON_VALUE)
public class BrukerController {

    private static final String FAVORITTER = "favoritter";

    private final BrukerService brukerService;
    private final MapperFacade mapperFacade;
    private final GetUserInfo getUserInfo;
    private final BrukerFavoritterRepository brukerFavoritterRepository;
    private final TeamRepository teamRepository;
    private final TeamBrukerRepository teamBrukerRepository;
    private final BrukerRepository brukerRepository;
    private final TeamService teamService;
    private final TestgruppeRepository testgruppeRepository;

    @Cacheable(CACHE_BRUKER)
    @GetMapping("/{brukerId}")
    @Transactional
    @Operation(description = "Hent Bruker med brukerId")
    public Mono<RsBruker> getBrukerBybrukerId(@PathVariable("brukerId") String brukerId) {

        return brukerService.fetchBrukerWithoutTeam(brukerId)
                .flatMap(this::mapFavoritter);
    }

    @Transactional
    @GetMapping("/current")
    @Operation(description = "Hent pålogget Bruker")
    public Mono<RsBruker> getCurrentBruker() {

        return brukerService.fetchBrukerWithoutTeam()
                .flatMap(this::mapFavoritter);
    }

    @Transactional
    @GetMapping
    @Operation(description = "Hent alle Brukerne")
    public Flux<RsBrukerUtenFavoritter> getAllBrukere() {

        return brukerService.fetchBrukere()
                .map(bruker -> mapperFacade.map(bruker, RsBrukerUtenFavoritter.class));
    }

    @Transactional
    @CacheEvict(value = {CACHE_BRUKER, CACHE_GRUPPE}, allEntries = true)
    @PutMapping("/leggTilFavoritt")
    @Operation(description = "Legg til Favoritt-testgruppe til pålogget Bruker")
    public Mono<RsBruker> leggTilFavoritt(@RequestBody RsBrukerUpdateFavoritterReq request) {

        return brukerService.leggTilFavoritt(request.getGruppeId())
                .flatMap(this::mapFavoritter);
    }

    @Transactional
    @CacheEvict(value = {CACHE_BRUKER, CACHE_GRUPPE}, allEntries = true)
    @PutMapping("/fjernFavoritt")
    @Operation(description = "Fjern Favoritt-testgruppe fra pålogget Bruker")
    public Mono<RsBruker> fjernFavoritt(@RequestBody RsBrukerUpdateFavoritterReq request) {

        return brukerService.fjernFavoritt(request.getGruppeId())
                .flatMap(this::mapFavoritter);
    }

    @Transactional
    @GetMapping("/teams")
    @Operation(description = "Hent alle team gjeldende bruker er medlem av")
    public Flux<RsTeamWithBrukere> getUserTeams() {

        return teamService.fetchTeam(brukerService::fetchTeamsForCurrentBruker)
                .flatMap(this::mapTeam);
    }

    @Transactional
    @CacheEvict(value = {CACHE_BRUKER, CACHE_GRUPPE}, allEntries = true)
    @PutMapping("/representererTeam/{teamId}")
    @Operation(description = "Sett aktivt team for innlogget bruker")
    public Mono<RsBruker> setRepresentererTeam(@PathVariable("teamId") Long teamId) {

        return brukerService.setRepresentererTeam(teamId)
                .flatMap(this::mapFavoritter);
    }

    @Transactional
    @CacheEvict(value = {CACHE_BRUKER, CACHE_GRUPPE}, allEntries = true)
    @DeleteMapping("/representererTeam")
    @Operation(description = "Fjern aktivt team for innlogget bruker")
    public Mono<RsBruker> clearRepresentererTeam() {

        return brukerService.setRepresentererTeam(null)
                .flatMap(this::mapFavoritter);
    }

    private Mono<RsTeamWithBrukere> mapTeam(Team team) {

        return brukerService.findById(team.getBrukerId())
                .zipWith(Mono.just(team))
                .map(tuple -> {
                    var context = MappingContextUtils.getMappingContext();
                    context.setProperty("brukerId", tuple.getT1().getBrukerId());
                    return mapperFacade.map(tuple.getT2(), RsTeamWithBrukere.class, context);
                });
    }

    private Mono<RsBruker> mapFavoritter(Bruker bruker) {

        return Mono.zip(
                        Mono.just(bruker),
                        (isNull(bruker.getRepresentererTeam()) ?
                                Mono.just(bruker) :
                                teamRepository.findById(bruker.getRepresentererTeam())
                                        .map(Team::getNavn)
                                        .flatMap(brukerRepository::findByBrukernavn))
                                .flatMap(bruker2 ->
                                        brukerFavoritterRepository.findByBrukerId(bruker2.getId())
                                                .map(BrukerFavoritter::getGruppeId)
                                                .collectList()
                                                .flatMapMany(testgruppeRepository::findByIdIn)
                                                .collectList()),
                        brukerRepository.findAll()
                                .reduce(new HashMap<Long, Bruker>(), (map, bruker1) -> {
                                    map.put(bruker1.getId(), bruker1);
                                    return map;
                                }),
                        getUserInfo.call(),
                        isNull(bruker.getRepresentererTeam()) ? Mono.just(new Team()) :
                                teamRepository.findById(bruker.getRepresentererTeam()),
                        isNull(bruker.getRepresentererTeam()) ? Mono.just(emptyList()) :
                                teamBrukerRepository.findByTeamId(bruker.getRepresentererTeam())
                                        .map(TeamBruker::getBrukerId)
                                        .collectList()
                                        .flatMapMany(brukerRepository::findByIdIn)
                                        .sort(Comparator.comparing(Bruker::getBrukernavn))
                                        .collectList(),
                        isNull(bruker.getRepresentererTeam()) ? Mono.just(new Bruker()) :
                                teamRepository.findById(bruker.getRepresentererTeam())
                                        .flatMap(team -> brukerService.findById(team.getBrukerId())))
                .map(tuple -> {
                    var context = MappingContextUtils.getMappingContext();
                    context.setProperty(FAVORITTER, tuple.getT2());
                    context.setProperty("alleBrukere", tuple.getT3());
                    context.setProperty("brukerInfo", tuple.getT4());
                    context.setProperty("representererTeam", tuple.getT5());
                    context.setProperty("teamMedlemmer", tuple.getT6());
                    context.setProperty("teamBrukerId", tuple.getT7().getBrukerId());
                    return mapperFacade.map(tuple.getT1(), RsBruker.class, context);
                });
    }
}
