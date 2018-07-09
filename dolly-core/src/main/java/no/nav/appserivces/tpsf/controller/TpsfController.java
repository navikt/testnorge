package no.nav.appserivces.tpsf.controller;

import no.nav.appserivces.tpsf.service.DollyTpsfService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/tpsf", produces = MediaType.APPLICATION_JSON_VALUE)
public class TpsfController {

    @Autowired
    DollyTpsfService dollyTpsfService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/opprett")
    public void opprettGruppe() {
        dollyTpsfService.opprettPersonerByKriterier(1L, null);
    }


}
