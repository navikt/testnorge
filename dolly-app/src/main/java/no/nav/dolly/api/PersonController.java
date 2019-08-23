package no.nav.dolly.api;

import static no.nav.dolly.bestilling.pdlforvalter.PdlForvalterClient.FALSK_IDENTITET;
import static no.nav.dolly.bestilling.pdlforvalter.PdlForvalterClient.KONTAKTINFORMASJON_DOEDSBO;
import static no.nav.dolly.provider.api.documentation.DocumentationNotes.AAREG_JSON_COMMENT;
import static no.nav.dolly.provider.api.documentation.DocumentationNotes.BOADRESSE_COMMENT;
import static no.nav.dolly.provider.api.documentation.DocumentationNotes.UTEN_ARBEIDSTAKER;

import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.service.DollyBestillingService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.RsBestilling;
import no.nav.dolly.domain.resultset.RsDollyUpdateRequest;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.TestgruppeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1/person")
public class PersonController {

    @Autowired
    private TestgruppeService testgruppeService;

    @Autowired
    private BestillingService bestillingService;

    @Autowired
    private DollyBestillingService dollyBestillingService;

    @Autowired
    private MapperFacade mapperFacade;

    @ApiOperation(value = "Endre/oppdater person i TPS og øvrige systemer", notes =
            BOADRESSE_COMMENT + AAREG_JSON_COMMENT + UTEN_ARBEIDSTAKER + KONTAKTINFORMASJON_DOEDSBO + FALSK_IDENTITET)
    @PutMapping("/{ident}")
    @ResponseStatus(HttpStatus.OK)
    public RsBestilling oppdaterPerson(@PathVariable String ident, @RequestBody RsDollyUpdateRequest request) {
        Bestilling bestilling = bestillingService.saveBestilling(ident, request);

        dollyBestillingService.oppdaterPersonAsync(ident, request, bestilling);
        return mapperFacade.map(bestilling, RsBestilling.class);
    }
}
