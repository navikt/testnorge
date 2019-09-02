package no.nav.registre.syntrest.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.response.AaregResponse;
import no.nav.registre.syntrest.services.AaregService;
import no.nav.registre.syntrest.utils.InputValidator;
import no.nav.registre.syntrest.utils.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("api/v1/generate")
@Api(description = "Endepunkter for å generere personer fra synt pakken. Tar seg også av å spinne opp og avslutte relevante synt-pakker på nais.")
@RequiredArgsConstructor
public class SyntController {
    private final AaregService aaregService;

    // TODO: format this in property file
    @PostMapping(value = "/aareg")
    @ApiOperation(value = "Aareg", notes = "Genererer syntetiske arbeidshistorikker bestående av meldinger på AAREG format.")
    @ApiResponses(
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = @ExampleProperty(value = "${aareg-example-response}", mediaType = "application/json")))
    )
    public ResponseEntity<AaregResponse> generateAareg(@ApiParam(value = "Liste med identifikasjonnumre for fikitve personer",
            examples = @Example(value = @ExampleProperty(value = "${aareg-example}" , mediaType = "application/json"))) @RequestBody List<String> fnrs) {
        try {
            InputValidator.validateInput(fnrs);
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ugyldig input.");
        }
        AaregResponse response = aaregService.generateData(fnrs);
        if (Objects.isNull(response)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Syntetisering feilet.");
        }

        return ResponseEntity.ok(response);
    }



}
