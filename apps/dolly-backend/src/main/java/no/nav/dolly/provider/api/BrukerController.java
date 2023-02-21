package no.nav.dolly.provider.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.dto.DeleteZIdentResponse;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerAndGruppeId;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUpdateFavoritterReq;
import no.nav.dolly.service.BrukerService;
import no.nav.dolly.service.TestgruppeService;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;
import static no.nav.dolly.config.CachingConfig.CACHE_BRUKER;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;
import static no.nav.dolly.util.CurrentAuthentication.getUserId;
import static org.apache.logging.log4j.util.Strings.isBlank;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/bruker", produces = MediaType.APPLICATION_JSON_VALUE)
public class BrukerController {

    private final BrukerService brukerService;
    private final MapperFacade mapperFacade;
    private final GetUserInfo getUserInfo;
    private final TestgruppeService testgruppeService;

    @Cacheable(CACHE_BRUKER)
    @GetMapping("/{brukerId}")
    @Operation(description = "Hent Bruker med brukerId")
    public RsBrukerAndGruppeId getBrukerBybrukerId(@PathVariable("brukerId") String brukerId) {
        Bruker bruker = brukerService.fetchBruker(brukerId);
        return mapperFacade.map(bruker, RsBrukerAndGruppeId.class);
    }

    @GetMapping("/current")
    @Operation(description = "Hent pålogget Bruker")
    public RsBruker getCurrentBruker() {
        Bruker bruker = brukerService.fetchOrCreateBruker(getUserId(getUserInfo));
        return mapperFacade.map(bruker, RsBruker.class);
    }

    @Transactional
    @PutMapping("/migrer")
    @Operation(description = "Legg til Nav Ident på ny Azure bruker")
    public int leggTilIdentPaaNyBruker(@RequestParam(required = false) String brukerId, @RequestParam Collection<String> navIdenter) {
        return brukerService.migrerBruker(navIdenter, isBlank(brukerId) ? getUserId(getUserInfo) : brukerId);
    }

    @Transactional
    @PutMapping("/fjernMigrering")
    @Operation(description = "Fjerner migrering av Azure bruker og denne brukerens relasjoner til Nav Identer")
    public int fjernMigrering(@RequestParam(required = false) String brukerId) {
        return brukerService.fjernMigreringAvBruker(isBlank(brukerId) ? getUserId(getUserInfo) : brukerId);
    }

    @Cacheable(CACHE_BRUKER)
    @GetMapping
    @Operation(description = "Hent alle Brukerne")
    public List<RsBrukerAndGruppeId> getAllBrukere() {
        return mapperFacade.mapAsList(brukerService.fetchBrukere(), RsBrukerAndGruppeId.class);
    }

    @Transactional
    @CacheEvict(value = {CACHE_BRUKER, CACHE_GRUPPE}, allEntries = true)
    @PutMapping("/leggTilFavoritt")
    @Operation(description = "Legg til Favoritt-testgruppe til pålogget Bruker")
    public RsBruker leggTilFavoritt(@RequestBody RsBrukerUpdateFavoritterReq request) {
        return mapperFacade.map(brukerService.leggTilFavoritt(request.getGruppeId()), RsBruker.class);
    }

    @Transactional
    @CacheEvict(value = {CACHE_BRUKER, CACHE_GRUPPE}, allEntries = true)
    @PutMapping("/fjernFavoritt")
    @Operation(description = "Fjern Favoritt-testgruppe fra pålogget Bruker")
    public RsBruker fjernFavoritt(@RequestBody RsBrukerUpdateFavoritterReq request) {
        return mapperFacade.map(brukerService.fjernFavoritt(request.getGruppeId()), RsBruker.class);
    }

    @Transactional
    @CacheEvict(value = {CACHE_BRUKER, CACHE_GRUPPE, CACHE_BESTILLING}, allEntries = true)
    @DeleteMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(description = "Fjern bruker identifisert med Z-id (IDA-bruker), og gjør nødvendig opprydding.<br>" +
            "Angi file som identifiserer Z-id brukere som skal slettes, en bruker per linje.")
    public List<DeleteZIdentResponse> sletteBrukereMedGrupper(
            @RequestParam("zBrukere") MultipartFile zBrukere) throws IOException {

        var brukere = new BufferedReader(new InputStreamReader(zBrukere.getInputStream(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.toSet());

        var slettedeGrupper = testgruppeService.sletteGrupperForIkkemigrerteNavIdenter(brukere);
        var slettedeBrukere = brukerService.slettNavIdentBrukere(brukere);

        log.info("Slettet antall Z-identer: {}", slettedeBrukere);
        return slettedeGrupper;
    }

    @CacheEvict(value = {CACHE_BRUKER, CACHE_GRUPPE, CACHE_BESTILLING}, allEntries = true)
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(description = "Oppdatere migrerte brukere <br>" +
            "Angi file som identifiserer Z-id brukere som skal oppdateres " +
            "tre kolonner: fra-bruker;Z-ident-navn;til-bruker")
    public List<String> oppdatereBrukere(
            @RequestParam("zBrukere") MultipartFile zBrukere) throws IOException {

        var brukere = new BufferedReader(new InputStreamReader(zBrukere.getInputStream(), StandardCharsets.UTF_8))
                .lines()
                .map(line -> line.split(";"))
                .map(felter -> ZBrukerDTO.builder()
                        .fraBruker(Long.getLong(felter[0]))
                        .navn(felter[1])
                        .tilBruker(Long.getLong(felter[2]))
                        .build())
                .map(testgruppeService::oppdaterBruker)
                .peek(ident -> log.info("Oppdatert Z-ident: {}", ident))
                .toList();

        log.info("Oppdatert antall Z-identer: {}", brukere.size());
        return brukere;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ZBrukerDTO {

        private Long fraBruker;
        private String navn;
        private Long tilBruker;
    }
}
