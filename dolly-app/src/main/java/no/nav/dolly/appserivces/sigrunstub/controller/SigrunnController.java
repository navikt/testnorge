package no.nav.dolly.appserivces.sigrunstub.controller;

import no.nav.dolly.domain.resultset.RsGrunnlagResponse;
import no.nav.dolly.domain.resultset.RsSigrunnOpprettSkattegrunnlag;
import no.nav.dolly.appserivces.sigrunstub.restcom.SigrunStubApiService;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/sigrun", produces = MediaType.APPLICATION_JSON_VALUE)
public class SigrunnController {

    @Autowired
    SigrunStubApiService sigrunStubApiService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public List<RsGrunnlagResponse> opprettInntekt(@RequestBody RsSigrunnOpprettSkattegrunnlag createTeamRequest) {
        return sigrunStubApiService.createInntektstuff(createTeamRequest);
    }

}
