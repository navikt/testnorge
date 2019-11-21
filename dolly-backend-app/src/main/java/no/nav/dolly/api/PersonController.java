package no.nav.dolly.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.service.DollyBestillingService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.RsDollyUpdateRequest;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestilling;
import no.nav.dolly.service.BestillingService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v1/person")
public class PersonController {

    private final BestillingService bestillingService;
    private final DollyBestillingService dollyBestillingService;
    private final MapperFacade mapperFacade;

    /**
     * @deprecated (På vent, ikke sikkert denne funksjonen skal tilbys)
     */
    @Deprecated
    @ApiOperation(value = "Endre/oppdatere person i TPS og øvrige systemer")
    @PutMapping("/{ident}")
    @ResponseStatus(HttpStatus.OK)
    public RsBestilling oppdaterPerson(@PathVariable String ident, @RequestBody RsDollyUpdateRequest request) {
        Bestilling bestilling = bestillingService.saveBestilling(ident, request);

        dollyBestillingService.oppdaterPersonAsync(ident, request, bestilling);
        return mapperFacade.map(bestilling, RsBestilling.class);
    }
}
