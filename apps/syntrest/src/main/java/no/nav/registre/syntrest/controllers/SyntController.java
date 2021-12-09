package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiException;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.syntrest.consumer.domain.SyntAmeldingConsumer;
import no.nav.registre.syntrest.consumer.SyntGetConsumer;
import no.nav.registre.syntrest.consumer.SyntPostConsumer;
import no.nav.registre.syntrest.utils.UrlUtils;

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

import no.nav.registre.syntrest.domain.aareg.Arbeidsforholdsmelding;
import no.nav.registre.syntrest.utils.InputValidator;

import no.nav.testnav.libs.domain.dto.aareg.amelding.Arbeidsforhold;
import no.nav.testnav.libs.domain.dto.aareg.amelding.ArbeidsforholdPeriode;

import static java.util.Objects.isNull;
import static no.nav.registre.syntrest.utils.UrlUtils.createQueryString;

@Slf4j
@RestController
@RequestMapping("api/v1/generate")
@RequiredArgsConstructor
public class SyntController {

    ///////////// SYNT CONSUMERS //////////////
    private final SyntPostConsumer<List<String>, List<Arbeidsforholdsmelding>> aaregConsumer;
    private final SyntGetConsumer<List<String>> meldekortConsumer;
    private final SyntPostConsumer<Map<String, List<no.nav.registre.syntrest.domain.inntekt.Inntektsmelding>>,
            Map<String, List<no.nav.registre.syntrest.domain.inntekt.Inntektsmelding>>> inntektConsumer;
    private final SyntAmeldingConsumer ameldingConsumer;

    private final UrlUtils urlUtils;

    @PostMapping("/aareg")
    @ApiOperation(value = "Aareg", notes = "Genererer syntetiske arbeidshistorikker bestående av meldinger på AAREG format.")
    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-aareg" })
    public ResponseEntity<List<Arbeidsforholdsmelding>> generateAareg(
            @ApiParam(value = "Liste med identifikasjonsnumre for fikitve personer", required = true)
            @RequestBody List<String> fnrs
    ) throws ApiException, InterruptedException {
        InputValidator.validateInput(fnrs);
        var response = aaregConsumer.synthesizeData(fnrs);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/meldekort/{meldegruppe}")
    @ApiOperation(value = "Meldekort", notes = "Opprett et antall meldekort for meldegruppen\n\nAPIet genererer " +
            "meldekort i XML format som kan settes inn i arena. Merk at denne metoden genererer kun bodyen til " +
            "XML-meldingen til et meldekort, og er avhengig av å få satt på et hode for at denne kan legges inn i " +
            "Arena.\n\nKernel density modellene blir generert ved førstegangs kjøring og lagret i postgresdatabasen, " +
            "synthdata_register_syn. Hvis modellene skal trenes på nytt igjen og oppdateres må disse slettes fra " +
            "postgresdatabasen og legges inn på nytt.\n\nMerk at dette ikke er mulig med dagens NAIS oppsett da dette " +
            "tar lang tid og applikasjonen timer ut. Dette må enten gjøres lokalt med samme python versjon som blir " +
            "kjørt på NAIS (3.7.1 per dags dato), eller så må NAIS instillingene oppdateres.")
    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-meldekort" })
    public ResponseEntity<List<String>> generateMeldekort(
            @ApiParam(value = "Meldegruppe", required = true)
            @PathVariable String meldegruppe,
            @ApiParam(value = "Antall meldinger", required = true)
            @RequestParam int numToGenerate,
            @ApiParam(value = "Verdi som vil overskrive alle ArbeidetTimerSum i meldekort")
            @RequestParam(required = false) Double arbeidstimer
    ) throws ApiException, InterruptedException {
        InputValidator.validateInput(numToGenerate);
        InputValidator.validateInput(InputValidator.INPUT_STRING_TYPE.MELDEGRUPPE, meldegruppe);

        var expandedPath = urlUtils.expandPath(meldekortConsumer.getUrl(), String.valueOf(numToGenerate), meldegruppe);
        List<String> response;
        if (isNull(arbeidstimer)) {
            response = meldekortConsumer.synthesizeData(expandedPath);
        } else {
            var queryString = createQueryString("arbeidstimer", arbeidstimer.toString(), "");
            response = meldekortConsumer.synthesizeData(expandedPath, queryString);
        }
        doResponseValidation(response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/inntekt")
    @ApiOperation(value = "Inntektsmeldinger (inntektstub)", notes = "Generer inntektsmeldinger på et map med fødselsnumre og " +
            "inntektsmeldinger på samme format som i inntektstub. \n\nHvis man legger ved en liste med inntektsmeldinger " +
            "per fødselsnummer, (altså forrige måneds inntektsmelding) blir den nye inntektsmeldingen basert på disse. " +
            "Hvis man legger ved en tom liste til fødselsnummeret blir en inntektsmelding generert basert på en kernel " +
            "density model.")
    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-inntekt" })
    public ResponseEntity<Map<String, List<no.nav.registre.syntrest.domain.inntekt.Inntektsmelding>>> generateInntektsMelding(
            @ApiParam(value = "Map der key=fødselsnummer, value=liste med inntektsmeldinger", required = true)
            @RequestBody Map<String, List<no.nav.registre.syntrest.domain.inntekt.Inntektsmelding>> fnrInntektMap
    ) throws InterruptedException, ApiException {
        InputValidator.validateInput(new ArrayList<>(fnrInntektMap.keySet()));
        var response = inntektConsumer.synthesizeData(fnrInntektMap);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/amelding/arbeidsforhold")
    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-amelding" })
    public ResponseEntity<List<Arbeidsforhold>> generateArbeidforholdHistorikk(
            @RequestBody Arbeidsforhold tidligereArbeidsforhold,
            @ApiParam(value = "Verdi bestemmer om det skal være mulig at avvik blir generert i arbeidforhold.")
            @RequestParam(required = false) String avvik,
            @ApiParam(value = "Verdi bestemmer om det skal være mulig at sluttdato blir generert i arbeidforhold.")
            @RequestParam(required = false) String sluttdato
    ) throws InterruptedException, ApiException {
        var queryString = createQueryString("avvik", avvik, "");
        queryString = createQueryString("sluttdato", sluttdato, queryString);

        var response = ameldingConsumer.synthesizeArbeidsforholdHistorikk(tidligereArbeidsforhold, queryString);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/amelding/arbeidsforhold/historikk")
    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-amelding" })
    public ResponseEntity<List<List<Arbeidsforhold>>> generateArbeidforholdHistorikkV2(
            @RequestBody List<Arbeidsforhold> tidligereArbeidsforhold,
            @ApiParam(value = "Verdi bestemmer om det skal være mulig at avvik blir generert i arbeidforhold.")
            @RequestParam(required = false) String avvik,
            @ApiParam(value = "Verdi bestemmer om det skal være mulig at sluttdato blir generert i arbeidforhold.")
            @RequestParam(required = false) String sluttdato
    ) throws InterruptedException, ApiException {
        var queryString = createQueryString("avvik", avvik, "");
        queryString = createQueryString("sluttdato", sluttdato, queryString);

        var response = ameldingConsumer.synthesizeArbeidsforholdHistorikk(tidligereArbeidsforhold, queryString);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/amelding/arbeidsforhold/start")
    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-amelding" })
    public ResponseEntity<List<Arbeidsforhold>> generateArbeidforholdStart(
            @RequestBody List<String> startdatoer,
            @ApiParam(value = "Verdi bestemmer om det skal være mulig at avvik blir generert i arbeidforhold.")
            @RequestParam(required = false) String avvik
    ) throws InterruptedException, ApiException {
        var queryString = createQueryString("avvik", avvik, "");
        var response = ameldingConsumer.synthesizeArbeidsforholdStart(startdatoer, queryString);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/amelding/arbeidsforhold/start/{arbeidsforholdType}")
    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-amelding" })
    public ResponseEntity<Arbeidsforhold> generateArbeidforholdMedType(
            @ApiParam(value = "Arbeidsforhold type.", required = true)
            @PathVariable String arbeidsforholdType,
            @RequestBody ArbeidsforholdPeriode request,
            @ApiParam(value = "Verdi bestemmer om det skal være mulig at avvik blir generert i arbeidforhold.")
            @RequestParam(required = false) String avvik
    ) throws InterruptedException, ApiException {
        InputValidator.validateInput(InputValidator.INPUT_STRING_TYPE.ARBEIDSFORHOLD_TYPE, arbeidsforholdType);
        var queryString = createQueryString("avvik", avvik, "");
        var response = ameldingConsumer.synthesizeArbeidsforholdStart(request, arbeidsforholdType, queryString);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/amelding/arbeidsforhold/initial")
    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-amelding" })
    public ResponseEntity<List<Arbeidsforhold>> generateArbeidforholdStartV2(
            @RequestBody ArbeidsforholdPeriode request,
            @ApiParam(value = "Verdi bestemmer om det skal være mulig at avvik blir generert i arbeidforhold.")
            @RequestParam(required = false) String avvik
    ) throws InterruptedException, ApiException {
        var queryString = createQueryString("avvik", avvik, "");
        var response = ameldingConsumer.synthesizeArbeidsforholdStart(request, queryString);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    private void doResponseValidation(Object response) {
        if (isNull(response)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Syntetisering feilet.");
        }
    }
}
