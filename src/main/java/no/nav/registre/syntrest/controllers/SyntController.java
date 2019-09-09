package no.nav.registre.syntrest.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jdk.internal.util.xml.impl.Input;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.response.AaregResponse;
import no.nav.registre.syntrest.response.ArenaAAPResponse;
import no.nav.registre.syntrest.response.BisysResponse;
import no.nav.registre.syntrest.response.domain.AAP115Melding;
import no.nav.registre.syntrest.response.domain.AAPMelding;
import no.nav.registre.syntrest.services.AaregService;
import no.nav.registre.syntrest.services.ArenaAAPService;
import no.nav.registre.syntrest.services.BisysService;
import no.nav.registre.syntrest.utils.InputValidator;
import no.nav.registre.syntrest.utils.Validation;
import no.nav.registre.syntrest.utils.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final ArenaAAPService arenaAAPService;
    private final BisysService bisysService;

    @PostMapping("/aareg")
    @ApiOperation(value = "Aareg", notes = "Genererer syntetiske arbeidshistorikker bestående av meldinger på AAREG format.")
    public ResponseEntity<List<AaregResponse>> generateAareg(
            @ApiParam(value = "Liste med identifikasjonnumre for fikitve personer")
            @RequestBody List<String> fnrs) {

        InputValidator.validateInput(fnrs);
        List<AaregResponse> response = aaregService.generateData(fnrs);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/arena/aap/11_5")
    @ApiOperation(value = "Aap115", notes = "Generer et antall AAP11_5 meldinger")
    public ResponseEntity<List<AAP115Melding>> generateAAP11_5(
            @ApiParam("Antall AAP11_5 meldinger")
            @RequestParam Integer numToGenerate) {

        InputValidator.validateInput(numToGenerate);
        List<AAP115Melding> response = arenaAAPService.generate115Data(numToGenerate);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/arena/aap/nyRettighet")
    @ApiOperation(value = "Ny Rettighet", notes = "Generer et antall nye rettigheter")
    public ResponseEntity<List<AAPMelding>> generateAAPNyRettighet(
            @ApiParam("Antall AAP meldinger/nye rettigheter")
            @RequestParam Integer numToGenerate) {

        InputValidator.validateInput(numToGenerate);
        List<AAPMelding> response = arenaAAPService.generateNyRettighetData(numToGenerate);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/bisys")
    @ApiOperation(value = "bisys")
    public ResponseEntity<List<BisysResponse>> generateBisys(
            @ApiParam("Antall meldinger")
            @RequestParam int numToGenerate){

        InputValidator.validateInput(numToGenerate);
        List<BisysResponse> response = bisysService.generateData(numToGenerate);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }


    private void doResponseValidation(Object response) {
        if (Objects.isNull(response)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Syntetisering feilet.");
        }
    }
}
