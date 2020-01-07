package no.nav.dolly.provider.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.resultset.RsDollyRelationRequest;
import no.nav.dolly.domain.resultset.entity.bestilling.RsRelasjonStatus;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.service.BestillingService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v1/ident")
public class RelasjonController {

    private final BestillingService bestillingService;

    @ApiOperation(value = "Koble eksisterende personer i Dolly ")
    @PutMapping("/{ident}/relasjon")
    @ResponseStatus(HttpStatus.OK)
    public RsRelasjonStatus koblePerson(@ApiParam(value = "Ident for hovedperson", required = true)
    @PathVariable("ident") String ident,
            @RequestBody RsDollyRelationRequest request) {

        throw new DollyFunctionalException("Funksjonen er ikke implementert");
//        Bestilling bestilling = bestillingService.saveBestilling(request);
//
//        dollyBestillingService.oppdaterPersonAsync(request, bestilling);
//        return mapperFacade.map(bestilling, RsBestillingStatus.class);
    }
}
