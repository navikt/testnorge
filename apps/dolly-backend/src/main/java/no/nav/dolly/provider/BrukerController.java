package no.nav.dolly.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerAndGruppeId;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUpdateFavoritterReq;
import no.nav.dolly.domain.resultset.entity.team.RsTeamWithBrukere;
import no.nav.dolly.service.BrukerService;
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
        var bruker = brukerService.fetchBrukerOrTeamBruker(brukerId);
        return mapperFacade.map(bruker, RsBrukerAndGruppeId.class);
    }

    @Transactional
    @GetMapping("/current")
    @Operation(description = "Hent pålogget Bruker")
    public RsBruker getCurrentBruker() {
        var bruker = brukerService.fetchCurrentBrukerWithoutTeam();
        return mapperFacade.map(bruker, RsBruker.class);
    }

    @Transactional(readOnly = true)
    @GetMapping
    @Operation(description = "Hent alle Brukerne")
    public List<RsBrukerAndGruppeId> getAllBrukere() {
        var brukere = brukerService.fetchBrukere();
        return mapperFacade.mapAsList(brukere, RsBrukerAndGruppeId.class);
    }

    @Transactional
    @CacheEvict(value = { CACHE_BRUKER, CACHE_GRUPPE }, allEntries = true)
    @PutMapping("/leggTilFavoritt")
    @Operation(description = "Legg til Favoritt-testgruppe til pålogget Bruker")
    public RsBruker leggTilFavoritt(@RequestBody RsBrukerUpdateFavoritterReq request) {
        return mapperFacade.map(brukerService.leggTilFavoritt(request.getGruppeId()), RsBruker.class);
    }

    @Transactional
    @CacheEvict(value = { CACHE_BRUKER, CACHE_GRUPPE }, allEntries = true)
    @PutMapping("/fjernFavoritt")
    @Operation(description = "Fjern Favoritt-testgruppe fra pålogget Bruker")
    public RsBruker fjernFavoritt(@RequestBody RsBrukerUpdateFavoritterReq request) {
        return mapperFacade.map(brukerService.fjernFavoritt(request.getGruppeId()), RsBruker.class);
    }

    @Transactional(readOnly = true)
    @GetMapping("/teams")
    @Operation(description = "Hent alle team gjeldende bruker er medlem av")
    public List<RsTeamWithBrukere> getUserTeams() {
        return mapperFacade.mapAsList(brukerService.fetchTeamsForCurrentBruker(), RsTeamWithBrukere.class);
    }

    @Transactional
    @CacheEvict(value = { CACHE_BRUKER, CACHE_GRUPPE }, allEntries = true)
    @PutMapping("/representererTeam/{teamId}")
    @Operation(description = "Sett aktivt team for innlogget bruker")
    public RsBruker setRepresentererTeam(@PathVariable("teamId") Long teamId) {
        var bruker = brukerService.setRepresentererTeam(teamId);
        return mapperFacade.map(bruker, RsBruker.class);
    }

    @Transactional
    @CacheEvict(value = { CACHE_BRUKER, CACHE_GRUPPE }, allEntries = true)
    @DeleteMapping("/representererTeam")
    @Operation(description = "Fjern aktivt team for innlogget bruker")
    public RsBruker clearRepresentererTeam() {
        var bruker = brukerService.setRepresentererTeam(null);
        return mapperFacade.map(bruker, RsBruker.class);
    }
}
