package no.nav.dolly.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.service.GjenopprettIdentService;
import no.nav.dolly.bestilling.service.OppdaterPersonService;
import no.nav.dolly.domain.resultset.RsDollyUpdateRequest;
import no.nav.dolly.domain.resultset.RsIdentBeskrivelse;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingStatus;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrdreStatus;
import no.nav.dolly.domain.resultset.entity.testident.RsWhereAmI;
import no.nav.dolly.domain.testperson.IdentAttributesResponse;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.NavigasjonService;
import no.nav.dolly.service.OrdreService;
import no.nav.testnav.libs.dto.dolly.v1.FinnesDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v1/ident")
public class TestpersonController {

    private final BestillingService bestillingService;
    private final GjenopprettIdentService gjenopprettIdentService;
    private final OppdaterPersonService oppdaterPersonService;
    private final MapperFacade mapperFacade;
    private final IdentService identService;
    private final NavigasjonService navigasjonService;
    private final OrdreService ordreService;

    @Operation(description = "Legge til egenskaper på person/endre person i TPS og øvrige systemer")
    @PutMapping("/{ident}/leggtilpaaperson")
    @CacheEvict(value = {CACHE_GRUPPE, CACHE_BESTILLING}, allEntries = true)
    @ResponseStatus(HttpStatus.OK)
    public Mono<RsBestillingStatus> endrePerson(@PathVariable String ident, @RequestBody RsDollyUpdateRequest request) {

        return bestillingService.saveBestilling(request, ident)
                .map(bestilling -> {
                    oppdaterPersonService.oppdaterPersonAsync(request, bestilling);
                    return mapperFacade.map(bestilling, RsBestillingStatus.class);
                });
    }

    @CacheEvict(value = CACHE_GRUPPE, allEntries = true)
    @Operation(description = "Endre status beskrivelse på testperson")
    @PutMapping("/{ident}/beskrivelse")
    @ResponseStatus(HttpStatus.OK)
    public Mono<IdentAttributesResponse> oppdaterTestidentBeskrivelse(@PathVariable String ident, @RequestBody RsIdentBeskrivelse beskrivelse) {

        return identService.saveIdentBeskrivelse(ident, beskrivelse.getBeskrivelse())
                .map(testident -> mapperFacade.map(testident, IdentAttributesResponse.class));
    }

    @CacheEvict(value = CACHE_GRUPPE, allEntries = true)
    @Operation(description = "Endre status \"i-bruk\" på testperson")
    @PutMapping("/{ident}/ibruk")
    @ResponseStatus(HttpStatus.OK)
    public Mono<IdentAttributesResponse> oppdaterTestidentIbruk(@PathVariable String ident, @RequestParam boolean iBruk) {

        return identService.saveIdentIBruk(ident, iBruk)
                .map(updatedIdent -> mapperFacade.map(updatedIdent, IdentAttributesResponse.class));
    }

    @Operation(description = "Gjenopprett test ident")
    @CacheEvict(value = {CACHE_BESTILLING, CACHE_GRUPPE}, allEntries = true)
    @Transactional
    @PostMapping("/gjenopprett/{ident}")
    public Mono<RsBestillingStatus> gjenopprettTestident(@PathVariable String ident, @RequestParam(required = false) List<String> miljoer) {

        return bestillingService.createBestillingForGjenopprettFraIdent(ident, miljoer)
                .map(bestilling -> {
                    gjenopprettIdentService.executeAsync(bestilling);
                    return mapperFacade.map(bestilling, RsBestillingStatus.class);
                });
    }

    @Operation(description = "Slett test ident")
    @CacheEvict(value = {CACHE_BESTILLING, CACHE_GRUPPE}, allEntries = true)
    @Transactional
    @DeleteMapping("/{ident}")
    public Mono<Void> deleteTestident(@PathVariable String ident) {

        return identService.slettTestident(ident);
    }

    @Operation(description = "Naviger til ønsket testperson")
    @Transactional
    @GetMapping("/naviger/{ident}")
    public Mono<RsWhereAmI> navigerTilTestident(@PathVariable String ident,
                                                 @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {

        return navigasjonService.navigerTilIdent(ident, pageSize);
    }

    @Operation(description = "Sjekk om ønsket testperson finnes i Dolly")
    @GetMapping("/finnes/{ident}")
    public Mono<Boolean> finnesTestident(@PathVariable String ident) {

        return identService.exists(ident);
    }

    @Operation(description = "Sjekk om testpersoner finnes i Dolly")
    @GetMapping("/finnes")
    public Mono<FinnesDTO> finnesTestident(@RequestParam List<String> identer) {

        return identService.exists(identer);
    }

    @Operation(description = "Send ønsket testperson til miljø")
    @Transactional
    @PostMapping("/ident/{ident}/ordre")
    public Mono<RsOrdreStatus> sendOrdre(@PathVariable String ident) {

        return ordreService.sendOrdre(ident);
    }
}