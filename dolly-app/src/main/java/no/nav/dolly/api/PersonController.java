package no.nav.dolly.api;

import static no.nav.dolly.api.AaregController.AAREG_JSON_COMMENT;
import static no.nav.dolly.api.TestgruppeController.BOADRESSE_COMMENT;
import static no.nav.dolly.api.TestgruppeController.FALSK_IDENTITET;
import static no.nav.dolly.api.TestgruppeController.KONTAKTINFORMASJON_DOEDSBO;
import static no.nav.dolly.api.TestgruppeController.UTEN_ARBEIDSTAKER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.service.DollyBestillingService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.RsBestilling;
import no.nav.dolly.domain.resultset.RsDollyUpdateRequest;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;

@RestController
@RequestMapping(value = "api/v1/person")
public class PersonController {

    @Autowired
    private IdentService identService;

    @Autowired
    private BestillingService bestillingService;

    @Autowired
    private DollyBestillingService dollyBestillingService;

    @Autowired
    private MapperFacade mapperFacade;

    /**
     * @deprecated (På vent, ikke sikkert denne funksjonen skal tilbys)
     */
    @Deprecated
    @ApiOperation(value = "Endre/oppdatere person i TPS og øvrige systemer", notes =
            BOADRESSE_COMMENT + AAREG_JSON_COMMENT + UTEN_ARBEIDSTAKER + KONTAKTINFORMASJON_DOEDSBO + FALSK_IDENTITET)
    @PutMapping("/{ident}")
    @ResponseStatus(HttpStatus.OK)
    public RsBestilling oppdaterPerson(@PathVariable String ident, @RequestBody RsDollyUpdateRequest request) {
        Bestilling bestilling = bestillingService.saveBestilling(ident, request);

        dollyBestillingService.oppdaterPersonAsync(ident, request, bestilling);
        return mapperFacade.map(bestilling, RsBestilling.class);
    }
}
