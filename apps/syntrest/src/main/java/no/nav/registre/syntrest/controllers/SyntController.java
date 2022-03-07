package no.nav.registre.syntrest.controllers;

import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.syntrest.consumer.SyntMeldekortConsumer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import no.nav.registre.syntrest.utils.InputValidator;

import static java.util.Objects.isNull;

@Slf4j
@RestController
@RequestMapping("api/v1/generate")
@RequiredArgsConstructor
public class SyntController {

    private final SyntMeldekortConsumer meldekortConsumer;

    @GetMapping("/meldekort/{meldegruppe}")
    @ApiOperation(value = "Meldekort", notes = "Opprett et antall meldekort for meldegruppen\n\nAPIet genererer " +
            "meldekort i XML format som kan settes inn i arena. Merk at denne metoden genererer kun bodyen til " +
            "XML-meldingen til et meldekort, og er avhengig av å få satt på et hode for at denne kan legges inn i " +
            "Arena.\n\nKernel density modellene blir generert ved førstegangs kjøring og lagret i postgresdatabasen, " +
            "synthdata_register_syn. Hvis modellene skal trenes på nytt igjen og oppdateres må disse slettes fra " +
            "postgresdatabasen og legges inn på nytt.\n\nMerk at dette ikke er mulig med dagens NAIS oppsett da dette " +
            "tar lang tid og applikasjonen timer ut. Dette må enten gjøres lokalt med samme python versjon som blir " +
            "kjørt på NAIS (3.7.1 per dags dato), eller så må NAIS instillingene oppdateres.")
    @Timed(value = "syntrest.resource.latency", extraTags = {"operation", "synthdata-meldekort"})
    public ResponseEntity<List<String>> generateMeldekort(
            @ApiParam(value = "Meldegruppe", required = true)
            @PathVariable String meldegruppe,
            @ApiParam(value = "Antall meldinger", required = true)
            @RequestParam int numToGenerate,
            @ApiParam(value = "Verdi som vil overskrive alle ArbeidetTimerSum i meldekort")
            @RequestParam(required = false) Double arbeidstimer
    ) {
        InputValidator.validateInput(numToGenerate);
        InputValidator.validateInput(InputValidator.INPUT_STRING_TYPE.MELDEGRUPPE, meldegruppe);

        List<String> response;
        if (isNull(arbeidstimer)) {
            response = meldekortConsumer.getSyntheticMeldekort(meldegruppe, numToGenerate);
        } else {
            response = meldekortConsumer.getSyntheticMeldekort(meldegruppe, numToGenerate, arbeidstimer.toString());
        }
        doResponseValidation(response);
        return ResponseEntity.ok(response);
    }

    private void doResponseValidation(Object response) {
        if (isNull(response)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Syntetisering feilet.");
        }
    }
}
