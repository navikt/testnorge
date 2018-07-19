package no.nav.appserivces.tpsf.controller;

import ma.glasnost.orika.MapperFacade;
import no.nav.appserivces.tpsf.domain.request.RsBestilling;
import no.nav.appserivces.tpsf.domain.request.RsDollyBestillingsRequest;
import no.nav.appserivces.tpsf.service.DollyTpsfService;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.jpa.Bestilling;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class TpsfController {

    @Autowired
    DollyTpsfService dollyTpsfService;

    @Autowired
    BestillingRepository bestillingRepository;

    @Autowired
    MapperFacade mapperFacade;

    @PostMapping("/gruppe/{gruppeId}/bestilling")
    public RsBestilling opprettGruppe(@PathVariable("gruppeId") Long gruppeId, @RequestBody RsDollyBestillingsRequest request) {
        Bestilling bestilling = bestillingRepository.save(new Bestilling());
        dollyTpsfService.opprettPersonerByKriterier(gruppeId, request, bestilling.getId());
        return mapperFacade.map(bestilling, RsBestilling.class);
    }
}
