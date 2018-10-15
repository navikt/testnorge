package no.nav.dolly.appservices.sigrunstub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import no.nav.dolly.appservices.sigrunstub.restcom.SigrunStubApiService;
import no.nav.dolly.domain.resultset.RsSigrunnOpprettSkattegrunnlag;

@RestController
@RequestMapping(value = "/api/v1/sigrun", produces = MediaType.APPLICATION_JSON_VALUE)
public class SigrunnController {

    @Autowired
    SigrunStubApiService sigrunStubApiService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void opprettInntekt(@RequestBody RsSigrunnOpprettSkattegrunnlag createTeamRequest) {
        sigrunStubApiService.createSkattegrunnlag(createTeamRequest);
    }

}
