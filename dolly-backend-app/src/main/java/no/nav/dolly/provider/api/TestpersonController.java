package no.nav.dolly.provider.api;

import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.service.DollyBestillingService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.RsDollyUpdateRequest;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingStatus;
import no.nav.dolly.domain.testperson.IdentAttributesResponse;
import no.nav.dolly.domain.testperson.IdentBeskrivelse;
import no.nav.dolly.domain.testperson.IdentIbruk;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v1/ident")
public class TestpersonController {

    private final BestillingService bestillingService;
    private final DollyBestillingService dollyBestillingService;
    private final MapperFacade mapperFacade;
    private final IdentService identService;

    /**
     * @deprecated (På vent, ikke sikkert denne funksjonen skal tilbys)
     */
    @Deprecated
    @ApiOperation(value = "Legge egenskaper på person/endre person i TPS og øvrige systemer")
    @PutMapping("/{ident}/leggtilpaaperson")
    @ResponseStatus(HttpStatus.OK)
    public RsBestillingStatus oppdaterPerson(@PathVariable String ident, @RequestBody RsDollyUpdateRequest request) {
        Bestilling bestilling = bestillingService.saveBestilling(ident, request);

        dollyBestillingService.oppdaterPersonAsync(ident, request, bestilling);
        return mapperFacade.map(bestilling, RsBestillingStatus.class);
    }

    @CacheEvict(value = CACHE_GRUPPE, allEntries = true)
    @ApiOperation(value = "Endre status beskrivelse på testperson")
    @PutMapping("/{ident}/beskrivelse")
    @ResponseStatus(HttpStatus.OK)
    public IdentAttributesResponse oppdaterTestidentBeskrivelse(@PathVariable String ident, @RequestParam String beskrivelse) {

        IdentBeskrivelse identBeskrivelse = IdentBeskrivelse.builder().beskrivelse(beskrivelse).build();
        identBeskrivelse.setIdent(ident);
        return mapperFacade.map(identService.save(identBeskrivelse), IdentAttributesResponse.class);
    }

    @CacheEvict(value = CACHE_GRUPPE, allEntries = true)
    @ApiOperation(value = "Endre status \"i-bruk\" på testperson")
    @PutMapping("/{ident}/ibruk")
    @ResponseStatus(HttpStatus.OK)
    public IdentAttributesResponse oppdaterTestidentIbruk(@PathVariable String ident, @RequestParam boolean iBruk) {

        IdentIbruk identIbruk = IdentIbruk.builder().ibruk(iBruk).build();
        identIbruk.setIdent(ident);
        return mapperFacade.map(identService.save(identIbruk), IdentAttributesResponse.class);
    }
}
