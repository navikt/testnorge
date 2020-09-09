package no.nav.dolly.provider.api;

import static no.nav.dolly.config.CachingConfig.CACHE_BRUKER;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;
import static no.nav.dolly.security.sts.StsOidcService.getUserPrinciple;

import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerAndGruppeId;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUpdateFavoritterReq;
import no.nav.dolly.service.BrukerService;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/bruker", produces = MediaType.APPLICATION_JSON_VALUE)
public class BrukerController {

    private final BrukerService brukerService;
    private final MapperFacade mapperFacade;

    @Cacheable(CACHE_BRUKER)
    @GetMapping("/{brukerId}")
    @ApiOperation(value = "Hent Bruker med brukerId", authorizations = { @Authorization(value = "Bearer token fra bruker") })
    public RsBrukerAndGruppeId getBrukerBybrukerId(@PathVariable("brukerId") String brukerId) {
        Bruker bruker = brukerService.fetchBruker(brukerId);
        return mapperFacade.map(bruker, RsBrukerAndGruppeId.class);
    }

    @GetMapping("/current")
    @ApiOperation(value = "Hent pålogget Bruker", authorizations = { @Authorization(value = "Bearer token fra bruker") })
    public RsBruker getCurrentBruker() {
        Bruker bruker = brukerService.fetchOrCreateBruker(getUserPrinciple());
        return mapperFacade.map(bruker, RsBruker.class);
    }

    @Cacheable(CACHE_BRUKER)
    @GetMapping
    @ApiOperation(value = "Hent alle Brukerne", authorizations = { @Authorization(value = "Bearer token fra bruker") })
    public List<RsBrukerAndGruppeId> getAllBrukere() {
        return mapperFacade.mapAsList(brukerService.fetchBrukere(), RsBrukerAndGruppeId.class);
    }

    @CacheEvict(value = { CACHE_BRUKER, CACHE_GRUPPE }, allEntries = true)
    @PutMapping("/leggTilFavoritt")
    @ApiOperation(value = "Legg til Favoritt-testgruppe til pålogget Bruker", authorizations = { @Authorization(value = "Bearer token fra bruker") })
    public RsBruker leggTilFavoritt(@RequestBody RsBrukerUpdateFavoritterReq request) {
        return mapperFacade.map(brukerService.leggTilFavoritt(request.getGruppeId()), RsBruker.class);
    }

    @CacheEvict(value = { CACHE_BRUKER, CACHE_GRUPPE }, allEntries = true)
    @PutMapping("/fjernFavoritt")
    @ApiOperation(value = "Fjern Favoritt-testgruppe fra pålogget Bruker", authorizations = { @Authorization(value = "Bearer token fra bruker") })
    public RsBruker fjernFavoritt(@RequestBody RsBrukerUpdateFavoritterReq request) {
        return mapperFacade.map(brukerService.fjernFavoritt(request.getGruppeId()), RsBruker.class);
    }
}
