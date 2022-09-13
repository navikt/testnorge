package no.nav.dolly.provider.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.service.DollyBestillingService;
import no.nav.dolly.bestilling.service.GjenopprettBestillingService;
import no.nav.dolly.domain.dto.TestidentDTO;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.RsDollyUpdateRequest;
import no.nav.dolly.domain.resultset.RsIdentBeskrivelse;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingStatus;
import no.nav.dolly.domain.resultset.entity.bestilling.RsOrdreStatus;
import no.nav.dolly.domain.resultset.entity.testident.RsWhereAmI;
import no.nav.dolly.domain.testperson.IdentAttributesResponse;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.NavigasjonService;
import no.nav.dolly.service.OrdreService;
import no.nav.dolly.service.PersonService;
import no.nav.dolly.service.TransaksjonMappingService;
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

import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v1/ident")
public class TestpersonController {

    private final BestillingService bestillingService;
    private final GjenopprettBestillingService gjenopprettBestillingService;
    private final TransaksjonMappingService transaksjonMappingService;
    private final DollyBestillingService dollyBestillingService;
    private final MapperFacade mapperFacade;
    private final IdentService identService;
    private final PersonService personService;
    private final NavigasjonService navigasjonService;
    private final OrdreService ordreService;

    @Operation(description = "Legge til egenskaper på person/endre person i TPS og øvrige systemer")
    @PutMapping("/{ident}/leggtilpaaperson")
    @CacheEvict(value = { CACHE_GRUPPE, CACHE_BESTILLING }, allEntries = true)
    @ResponseStatus(HttpStatus.OK)
    public RsBestillingStatus endrePerson(@PathVariable String ident, @RequestBody RsDollyUpdateRequest request) {

        if (nonNull(request.getPdldata())) {
            request.getPdldata().setOpprettNyPerson(null);
        }
        Bestilling bestilling = bestillingService.saveBestilling(request, ident);

        dollyBestillingService.oppdaterPersonAsync(request, bestilling);
        return mapperFacade.map(bestilling, RsBestillingStatus.class);
    }

    @CacheEvict(value = CACHE_GRUPPE, allEntries = true)
    @Operation(description = "Endre status beskrivelse på testperson")
    @PutMapping("/{ident}/beskrivelse")
    @ResponseStatus(HttpStatus.OK)
    public IdentAttributesResponse oppdaterTestidentBeskrivelse(@PathVariable String ident, @RequestBody RsIdentBeskrivelse beskrivelse) {

        return mapperFacade.map(identService.saveIdentBeskrivelse(ident, beskrivelse.getBeskrivelse()), IdentAttributesResponse.class);
    }

    @CacheEvict(value = CACHE_GRUPPE, allEntries = true)
    @Operation(description = "Endre status \"i-bruk\" på testperson")
    @PutMapping("/{ident}/ibruk")
    @ResponseStatus(HttpStatus.OK)
    public IdentAttributesResponse oppdaterTestidentIbruk(@PathVariable String ident, @RequestParam boolean iBruk) {

        return mapperFacade.map(identService.saveIdentIBruk(ident, iBruk), IdentAttributesResponse.class);
    }

    @Operation(description = "Gjenopprett test ident")
    @CacheEvict(value = { CACHE_BESTILLING, CACHE_GRUPPE }, allEntries = true)
    @Transactional
    @PostMapping("/gjenopprett/{ident}")
    public void gjenopprettTestident(@PathVariable String ident, @RequestParam List<String> miljoer) {

        if (!identService.exists(ident)) {
            throw new NotFoundException(format("Testperson med ident %s ble ikke funnet.", ident));
        }
        var bestillinger = bestillingService.fetchBestillingerByTestident(ident);
        if (bestillinger.isEmpty()) {
            throw new NotFoundException(format("Fant ingen bestillinger på ident %s", ident));
        }

        bestillinger.forEach(bestillingId -> {
            var bestilling = bestillingService.createBestillingForGjenopprettFraBestilling(bestillingId, miljoer);
            gjenopprettBestillingService.executeAsync(bestilling);
        });
    }

    @Operation(description = "Slett test ident")
    @CacheEvict(value = { CACHE_BESTILLING, CACHE_GRUPPE }, allEntries = true)
    @Transactional
    @DeleteMapping("/{ident}")
    public void deleteTestident(@PathVariable String ident) {

        if (!identService.exists(ident)) {
            throw new NotFoundException(format("Testperson med ident %s ble ikke funnet.", ident));
        }
        var testIdenter = mapperFacade.mapAsList(
                List.of(identService.getTestIdent(ident)), TestidentDTO.class);

        transaksjonMappingService.slettTransaksjonMappingByTestident(ident);
        bestillingService.slettBestillingByTestIdent(ident);
        identService.slettTestident(ident);

        personService.slettTestnorgeRelasjoner(testIdenter);
        personService.recyclePersoner(testIdenter);
    }

    @Operation(description = "Naviger til ønsket testperson")
    @Transactional
    @GetMapping("/naviger/{ident}")
    public RsWhereAmI navigerTilTestident(@PathVariable String ident) {

        return navigasjonService.navigerTilIdent(ident);
    }

    @Operation(description = "Send ønsket testperson til miljø")
    @Transactional
    @PostMapping("/ident/{ident}/ordre")
    public RsOrdreStatus sendOrdre(@PathVariable String ident) {

        return ordreService.sendOrdre(ident);
    }
}
