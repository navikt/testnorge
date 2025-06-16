package no.nav.dolly.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerAndClaims;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUpdateFavoritterReq;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.repository.BrukerFavoritterRepository;
import no.nav.dolly.service.BrukerService;
import no.nav.testnav.libs.reactivesecurity.action.GetUserInfo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    @Cacheable(CACHE_BRUKER)
    @GetMapping("/{brukerId}")
    @Transactional(readOnly = true)
    @Operation(description = "Hent Bruker med brukerId")
    public Mono<RsBruker> getBrukerBybrukerId(@PathVariable("brukerId") String brukerId) {

        return brukerService.fetchBruker(brukerId)
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
                .flatMap(bruker -> brukerFavoritterRepository.getAllByBrukerId(bruker.getId())
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

    private <T> Mono<T> getFavoritter(Bruker bruker, Class<T> clazz) {

        return Mono.zip(reactor.core.publisher.Mono.just(bruker),
                        brukerFavoritterRepository.getAllByBrukerId(bruker.getId())
                                .collectList(),
                        getUserInfo.call())
                .map(tuple -> {
                    var context = MappingContextUtils.getMappingContext();
                    context.setProperty("favoritter", tuple.getT2());
                    context.setProperty("brukerInfo", tuple.getT3());
                    return mapperFacade.map(tuple.getT1(), clazz, context);
                });
    }
}
