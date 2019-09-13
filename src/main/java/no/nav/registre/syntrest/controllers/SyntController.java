package no.nav.registre.syntrest.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.controllers.request.InntektsmeldingInntekt;
import no.nav.registre.syntrest.response.Arbeidsforholdsmelding;
import no.nav.registre.syntrest.response.Barnebidragsmelding;
import no.nav.registre.syntrest.response.InntektsmeldingPopp;
import no.nav.registre.syntrest.response.Medlemskapsmelding;
import no.nav.registre.syntrest.response.AAP115Melding;
import no.nav.registre.syntrest.response.AAPMelding;
import no.nav.registre.syntrest.response.Institusjonsmelding;
import no.nav.registre.syntrest.response.SamMelding;
import no.nav.registre.syntrest.response.SkdMelding;
import no.nav.registre.syntrest.response.TPmelding;
import no.nav.registre.syntrest.services.SyntetiseringService;
import no.nav.registre.syntrest.utils.InputValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("api/v1/generate")
//@Api(description = "Endepunkter for å generere personer fra synt pakken. Tar seg også av å spinne opp og avslutte relevante synt-pakker på nais.")
@RequiredArgsConstructor
public class SyntController {
    private final SyntetiseringService syntetiseringService;

    @PostMapping("/aareg")
    @ApiOperation(value = "Aareg", notes = "Genererer syntetiske arbeidshistorikker bestående av meldinger på AAREG format.")
    public ResponseEntity<List<Arbeidsforholdsmelding>> generateAareg(
            @ApiParam(value = "Liste med identifikasjonnumre for fikitve personer")
            @RequestBody List<String> fnrs
    ) {
        InputValidator.validateInput(fnrs);
        List<Arbeidsforholdsmelding> response = syntetiseringService.generateAaregData(fnrs);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/arena/aap/11_5")
    @ApiOperation(value = "Aap115", notes = "Generer et antall AAP11_5 meldinger")
    public ResponseEntity<List<AAP115Melding>> generateAAP11_5(
            @ApiParam("Antall AAP11_5 meldinger")
            @RequestParam Integer numToGenerate
    ) {
        InputValidator.validateInput(numToGenerate);
        List<AAP115Melding> response = syntetiseringService.generateAAP115Data(numToGenerate);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/arena/aap/nyRettighet")
    @ApiOperation(value = "Ny Rettighet", notes = "Generer et antall nye rettigheter")
    public ResponseEntity<List<AAPMelding>> generateAAPNyRettighet(
            @ApiParam("Antall AAP meldinger/nye rettigheter")
            @RequestParam Integer numToGenerate
    ) {
        InputValidator.validateInput(numToGenerate);
        List<AAPMelding> response = syntetiseringService.generateAAPData(numToGenerate);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/bisys")
    @ApiOperation(value = "bisys")
    public ResponseEntity<List<Barnebidragsmelding>> generateBisys(
            @ApiParam("Antall meldinger")
            @RequestParam int numToGenerate
    ) {
        InputValidator.validateInput(numToGenerate);
        List<Barnebidragsmelding> response = syntetiseringService.generateBisysData(numToGenerate);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/inst")
    @ApiOperation(value = "Inst", notes = "Generer institusjonsforhold")
    public ResponseEntity<List<Institusjonsmelding>> generateInst(
            @ApiParam("Antall institusjonsmeldinger")
            @RequestParam int numToGenerate
    ) {
        InputValidator.validateInput(numToGenerate);
        List<Institusjonsmelding> response = syntetiseringService.generateInstData(numToGenerate);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/medl")
    @ApiOperation(value = "Medl", notes = "Generer MEDL meldinger\nObs! Veldig treg!")
    public ResponseEntity<List<Medlemskapsmelding>> generateMedl(
            @ApiParam("Antall meldinger")
            @RequestParam int numToGenerate
    ) {
        InputValidator.validateInput(numToGenerate);
        List<Medlemskapsmelding> response = syntetiseringService.generateMedlData(numToGenerate);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/meldekort/{meldegruppe}")
    @ApiOperation(value = "Meldekort", notes = "Opprett et antall meldekort for meldegruppen\nReturnerer en liste med strenger der en hvert element er en meldekort xml.")
    public ResponseEntity<List<String>> generateMeldekort(
            @ApiParam("Meldegruppe")
            @PathVariable String meldegruppe,
            @ApiParam("Antall meldinger")
            @RequestParam int numToGenerate
    ) {
        InputValidator.validateInput(numToGenerate);
        InputValidator.validateInput(InputValidator.INPUT_STRING_TYPE.MELDEGRUPPE, meldegruppe);
        List<String> response = syntetiseringService.generateMeldekortData(numToGenerate, meldegruppe);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/nav/{endringskode}")
    @ApiOperation(value = "Nav Melding", notes = "Opprett et antall meldinger med endringskode fra path variabelen.\nReturenterer en liste med strenger der hvert element er en endringsmelding-xml.")
    public ResponseEntity<List<String>> generateNavEndringsmelding(
            @ApiParam("Nav endringskode")
            @PathVariable String endringskode,
            @ApiParam("Antall meldinger")
            @RequestParam int numToGenerate
    ) {
        InputValidator.validateInput(numToGenerate);
        InputValidator.validateInput(InputValidator.INPUT_STRING_TYPE.ENDRINGSKODE_NAV, endringskode);
        List<String> response = syntetiseringService.generateEndringsmeldingData(numToGenerate, endringskode);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/popp")
    @ApiOperation(value = "Inntektsmelding", notes = "Genererer syntetiske inntektsmeldinger til Sigrunstub. Inntektsmeldingene blir returnert på et format som kan bli lagret i sigrunstub, og vil generere en ny inntektsmelding basert på personens inntektsmelding forrige år. Hvis personen ikke har en inntektsmelding vil det bli samplet en ny inntektsmelding fra en BeAn/CART-modell.")
    public ResponseEntity<List<InntektsmeldingPopp>> generateInntektsmelding(
            @ApiParam("Fnrs å opprette inntektsmeldinger på")
            @RequestBody List<String> fnrs
    ) {
        InputValidator.validateInput(fnrs);
        List<InntektsmeldingPopp> response = syntetiseringService.generatePoppData(fnrs);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/sam")
    @ApiOperation(value = "Generer SAM melding", notes = "API for å generere syntetiserte SAM data.")
    public ResponseEntity<List<SamMelding>> generateSamMelding(
            @ApiParam("Antall meldinger")
            @RequestParam int numToGenerate
    ) {
        InputValidator.validateInput(numToGenerate);
        List<SamMelding> response = syntetiseringService.generateSamMeldingData(numToGenerate);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/inntekt")
    @ApiOperation(value = "Inntektsmeldinger", notes = "Generer inntektsmeldinger på en liste med fødselsnumre. Hvis man også legger ved en liste med inntektsmeldinger per fødselsnummer blir den nye inntektsmeldingen basert på disse.")
    public ResponseEntity<Map<String, List<InntektsmeldingInntekt>>> generateInntektsMelding(
            @ApiParam("Map der key: fødselsnummer, value: liste med inntektsmeldinger")
            @RequestBody Map<String, List<InntektsmeldingInntekt>> fnrInntektMap
    ) {
        InputValidator.validateInput(new ArrayList<>(fnrInntektMap.keySet()));
        Map<String, List<InntektsmeldingInntekt>> response = syntetiseringService.generateInntektData(fnrInntektMap);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/tp")
    @ApiOperation(value = "Tjeneste Pensjonsmeldinger", notes = "Generer antall tjenestepensjonsmeldinger")
    public ResponseEntity<List<TPmelding>> generateTPMelding(
            @ApiParam("Antall meldinger")
            @RequestParam int numToGenerate
    ) {
        InputValidator.validateInput(numToGenerate);
        List<TPmelding> response = syntetiseringService.generateTPData(numToGenerate);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/tps/{endringskode}")
    @ApiOperation(value = "Generer SKD melding", notes = "Lager SKD meldinger for ulike endringskoder")
    public ResponseEntity<List<SkdMelding>> generateSkdMelding(
            @ApiParam("Endringskode")
            @PathVariable String endringskode,
            @ApiParam("Antall meldinger")
            @RequestParam int numToGenerate
    ) {
        InputValidator.validateInput(InputValidator.INPUT_STRING_TYPE.ENDRINGSKODE, endringskode);
        InputValidator.validateInput(numToGenerate);
        List<SkdMelding> response = syntetiseringService.generateTPSData(numToGenerate, endringskode);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }




    private void doResponseValidation(Object response) {
        if (Objects.isNull(response)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Syntetisering feilet.");
        }
    }
}
