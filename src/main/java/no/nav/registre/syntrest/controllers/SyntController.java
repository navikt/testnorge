package no.nav.registre.syntrest.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ExampleProperty;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.response.AaregResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/generate")
@Api(description = "Endepunkter for å generere personer fra synt pakken. Tar seg også av å spinne opp og avslutte relevante synt-pakker på nais.")
public class SyntController {

    // TODO: format this in property file
    @PostMapping(value = "/aareg")
    @ApiOperation(value = "Aareg", notes = "Genererer syntetiske arbeidshistorikker bestående av meldinger på AAREG format.")
    public ResponseEntity<AaregResponse> generateAareg(@ApiParam(value = "Liste med identifikasjonnumre for fikitve personer",
            examples = @io.swagger.annotations.Example(
                    value = {
                            @ExampleProperty(value = "[\"12345678910\", \"10987654321\"]", mediaType = "application/json")
                    }
            )) @RequestBody String[] fnrs) {

           return null;
    }



}
