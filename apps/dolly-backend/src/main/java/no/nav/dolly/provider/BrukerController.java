package no.nav.dolly.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.TeamBruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerAndClaims;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUpdateFavoritterReq;
import no.nav.dolly.domain.resultset.entity.team.RsTeamWithBrukere;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.repository.BrukerFavoritterRepository;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TeamBrukerRepository;
import no.nav.dolly.repository.TeamRepository;
import no.nav.dolly.service.BrukerService;
import no.nav.testnav.libs.reactivesecurity.action.GetUserInfo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Propagation;
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

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toSet;
import static no.nav.dolly.config.CachingConfig.CACHE_BRUKER;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/bruker", produces = MediaType.APPLICATION_JSON_VALUE)
public class BrukerController {

    private final BrukerService brukerService;
    private final MapperFacade mapperFacade;
    private final GetUserInfo getUserInfo;
    private final BrukerFavoritterRepository brukerFavoritterRepository;
    private final TeamRepository teamRepository;
    private final TeamBrukerRepository teamBrukerRepository;
    private final BrukerRepository brukerRepository;

    @Cacheable(CACHE_BRUKER)
    @GetMapping("/{brukerId}")
    @Transactional(readOnly = true)
    @Operation(description = "Hent Bruker med brukerId")
    public Mono<RsBruker> getBrukerBybrukerId(@PathVariable("brukerId") String brukerId) {

        return brukerService.fetchOrCreateBruker(brukerId)
                .flatMap(bruker -> getFavoritter(bruker, RsBruker.class));
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, noRollbackFor = Exception.class)
    @GetMapping("/current")
    @Operation(description = "Hent pålogget Bruker")
    public Mono<RsBrukerAndClaims> getCurrentBruker() {

        return brukerService.fetchOrCreateBruker()
                .flatMap(bruker -> getFavoritter(bruker, RsBrukerAndClaims.class));
    }

    @Transactional(readOnly = true)
    @GetMapping
    @Operation(description = "Hent alle Brukerne")
    public Flux<RsBruker> getAllBrukere() {

        return brukerService.fetchBrukere()
                .flatMap(bruker -> brukerFavoritterRepository.findByBrukerId(bruker.getId())
                        .collectList()
                        .map(favoritter -> {
                            var context = MappingContextUtils.getMappingContext();
                            context.setProperty("favoritter", favoritter);
                            return mapperFacade.map(bruker, RsBruker.class, context);
                        }));
    }

    @Transactional
    @CacheEvict(value = {CACHE_BRUKER, CACHE_GRUPPE}, allEntries = true)
    @PutMapping("/leggTilFavoritt")
    @Operation(description = "Legg til Favoritt-testgruppe til pålogget Bruker")
    public Mono<RsBruker> leggTilFavoritt(@RequestBody RsBrukerUpdateFavoritterReq request) {

        return brukerService.leggTilFavoritt(request.getGruppeId())
                .map(bruker -> mapperFacade.map(bruker, RsBruker.class));
    }

    @Transactional
    @CacheEvict(value = {CACHE_BRUKER, CACHE_GRUPPE}, allEntries = true)
    @PutMapping("/fjernFavoritt")
    @Operation(description = "Fjern Favoritt-testgruppe fra pålogget Bruker")
    public Mono<RsBruker> fjernFavoritt(@RequestBody RsBrukerUpdateFavoritterReq request) {

        return brukerService.fjernFavoritt(request.getGruppeId())
                .map(bruker -> mapperFacade.map(bruker, RsBruker.class));
    }

    @Transactional(readOnly = true)
    @GetMapping("/teams")
    @Operation(description = "Hent alle team gjeldende bruker er medlem av")
    public Flux<RsTeamWithBrukere> getUserTeams() {

        return brukerService.fetchTeamsForCurrentBruker()
                .map(team -> mapperFacade.map(team, RsTeamWithBrukere.class));
    }

    @Transactional
    @CacheEvict(value = {CACHE_BRUKER, CACHE_GRUPPE}, allEntries = true)
    @PutMapping("/representererTeam/{teamId}")
    @Operation(description = "Sett aktivt team for innlogget bruker")
    public Mono<RsBruker> setRepresentererTeam(@PathVariable("teamId") Long teamId) {

        return brukerService.setRepresentererTeam(teamId)
                .map(bruker -> mapperFacade.map(bruker, RsBruker.class));
    }

    @Transactional
    @CacheEvict(value = {CACHE_BRUKER, CACHE_GRUPPE}, allEntries = true)
    @DeleteMapping("/representererTeam")
    @Operation(description = "Fjern aktivt team for innlogget bruker")
    public Mono<RsBruker> clearRepresentererTeam() {

        return brukerService.setRepresentererTeam(null)
                .map(bruker -> mapperFacade.map(bruker, RsBruker.class));
    }

    private <T> Mono<T> getFavoritter(Bruker bruker, Class<T> clazz) {

        return Mono.zip(Mono.just(bruker),
                        brukerFavoritterRepository.findByBrukerId(bruker.getId())
                                .collectList(),
                        getUserInfo.call(),
                        isNull(bruker.getRepresentererTeam()) ? Mono.empty() :
                                teamRepository.findById(bruker.getRepresentererTeam()),
                        isNull(bruker.getRepresentererTeam()) ? Mono.just(emptyList()) :
                                teamBrukerRepository.findByTeamId(bruker.getRepresentererTeam())
                                        .map(TeamBruker::getBrukerId)
                                        .collectList()
                                        .flatMapMany(brukerRepository::findByIdIn)
                                        .sort(Comparator.comparing(Bruker::getBrukernavn))
                                        .collect(toSet()))
                .map(tuple -> {
                    var context = MappingContextUtils.getMappingContext();
                    context.setProperty("favoritter", tuple.getT2());
                    context.setProperty("brukerInfo", tuple.getT3());
                    context.setProperty("representererTeam", tuple.getT4());
                    context.setProperty("teamMedlemmer", tuple.getT5());
                    return mapperFacade.map(tuple.getT1(), clazz, context);
                });
    }
}
