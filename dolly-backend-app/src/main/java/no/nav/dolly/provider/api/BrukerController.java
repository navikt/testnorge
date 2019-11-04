package no.nav.dolly.provider.api;

import static no.nav.dolly.config.CachingConfig.CACHE_BRUKER;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;

import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerAndGruppeId;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUpdateFavoritterReq;
import no.nav.dolly.service.BrukerService;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/bruker", produces = MediaType.APPLICATION_JSON_VALUE)
public class BrukerController {

    private final BrukerService brukerService;
    private final MapperFacade mapperFacade;

    @Cacheable(CACHE_BRUKER)
    @GetMapping("/{brukerId}")
    @ApiOperation("Hent Bruker med brukerId")
    public RsBrukerAndGruppeId getBrukerBybrukerId(@PathVariable("brukerId") String brukerId) {
        Bruker bruker = brukerService.fetchBruker(brukerId);
        return mapperFacade.map(bruker, RsBrukerAndGruppeId.class);
    }

    @GetMapping("/current")
    @ApiOperation("Hent pålogget Bruker")
    public RsBruker getCurrentBruker() {
        OidcTokenAuthentication auth = (OidcTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
        Bruker bruker = brukerService.fetchOrCreateBruker(auth.getPrincipal());
        return mapperFacade.map(bruker, RsBruker.class);
    }

    @Cacheable(CACHE_BRUKER)
    @GetMapping
    @ApiOperation("Hent alle Brukerne")
    public List<RsBrukerAndGruppeId> getAllBrukere() {
        return mapperFacade.mapAsList(brukerService.fetchBrukere(), RsBrukerAndGruppeId.class);
    }

    @CacheEvict(value = { CACHE_BRUKER, CACHE_GRUPPE }, allEntries = true)
    @PutMapping("/leggTilFavoritt")
    @ApiOperation("Legg til Favoritt-testgruppe til pålogget Bruker")
    public RsBruker leggTilFavoritt(@RequestBody RsBrukerUpdateFavoritterReq request) {
        return mapperFacade.map(brukerService.leggTilFavoritt(request.getGruppeId()), RsBruker.class);
    }

    @CacheEvict(value = { CACHE_BRUKER, CACHE_GRUPPE }, allEntries = true)
    @PutMapping("/fjernFavoritt")
    @ApiOperation("Fjern Favoritt-testgruppe fra pålogget Bruker")
    public RsBruker fjernFavoritt(@RequestBody RsBrukerUpdateFavoritterReq request) {
        return mapperFacade.map(brukerService.fjernFavoritt(request.getGruppeId()), RsBruker.class);
    }
}
