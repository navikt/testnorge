package no.nav.dolly.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerAndGruppeId;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUpdateFavoritterReq;
import no.nav.dolly.service.BrukerService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.RetryConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
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

import java.util.List;

import static no.nav.dolly.config.CachingConfig.CACHE_BRUKER;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/bruker", produces = MediaType.APPLICATION_JSON_VALUE)
public class BrukerController {

    private final BrukerService brukerService;
    private final MapperFacade mapperFacade;

    @Cacheable(CACHE_BRUKER)
    @GetMapping("/{brukerId}")
    @Transactional(readOnly = true)
    @Operation(description = "Hent Bruker med brukerId")
    public RsBrukerAndGruppeId getBrukerBybrukerId(@PathVariable("brukerId") String brukerId) {
        Bruker bruker = brukerService.fetchBruker(brukerId);
        return mapperFacade.map(bruker, RsBrukerAndGruppeId.class);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, noRollbackFor = Exception.class)
    @GetMapping("/current")
    @Operation(description = "Hent pålogget Bruker")
    public Mono<RsBruker> getCurrentBruker() {

        return brukerService.fetchOrCreateBruker()
                .map(bruker -> mapperFacade.map(bruker, RsBruker.class));
    }

    @Transactional(readOnly = true)
    @GetMapping
    @Operation(description = "Hent alle Brukerne")
    public Mono<List<RsBrukerAndGruppeId>> getAllBrukere() {

        return brukerService.fetchBrukere()
                .map(brukere -> mapperFacade.mapAsList(brukere, RsBrukerAndGruppeId.class));
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
}
