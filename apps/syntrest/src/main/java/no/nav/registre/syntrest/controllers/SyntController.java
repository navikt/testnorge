package no.nav.registre.syntrest.controllers;

import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.consumer.SyntConsumer;
import no.nav.registre.syntrest.consumer.UriExpander;
import no.nav.registre.syntrest.domain.amelding.Arbeidsforhold;
import no.nav.registre.syntrest.domain.aareg.Arbeidsforholdsmelding;
import no.nav.registre.syntrest.domain.bisys.Barnebidragsmelding;
import no.nav.registre.syntrest.domain.frikort.FrikortKvittering;
import no.nav.registre.syntrest.domain.inst.Institusjonsmelding;
import no.nav.registre.syntrest.domain.medl.Medlemskapsmelding;
import no.nav.registre.syntrest.domain.popp.Inntektsmelding;
import no.nav.registre.syntrest.domain.sam.SamMelding;
import no.nav.registre.syntrest.domain.tp.TPmelding;
import no.nav.registre.syntrest.domain.tps.SkdMelding;
import no.nav.registre.syntrest.utils.InputValidator;
import org.springframework.beans.factory.annotation.Value;
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
@RequiredArgsConstructor
public class SyntController {

    ///////////// SYNT CONSUMERS //////////////
    private final SyntConsumer aaregConsumer;
    private final SyntConsumer bisysConsumer;
    private final SyntConsumer instConsumer;
    private final SyntConsumer medlConsumer;
    private final SyntConsumer meldekortConsumer;
    private final SyntConsumer navConsumer;
    private final SyntConsumer poppConsumer;
    private final SyntConsumer samConsumer;
    private final SyntConsumer inntektConsumer;
    private final SyntConsumer tpConsumer;
    private final SyntConsumer tpsConsumer;
    private final SyntConsumer frikortConsumer;
    private final SyntConsumer aMeldingConsumer;


    ///////////// URLs //////////////
    @Value("${synth-aareg-url}")
    private String aaregUrl;
    @Value("${synth-arena-bisys-url}")
    private String bisysUrl;
    @Value("${synth-inst-url}")
    private String instUrl;
    @Value("${synth-medl-url}")
    private String medlUrl;
    @Value("${synth-arena-meldekort-url}")
    private String arenaMeldekortUrl;
    @Value("${synth-nav-url}")
    private String navEndringsmeldingUrl;
    @Value("${synth-popp-url}")
    private String poppUrl;
    @Value("${synth-sam-url}")
    private String samUrl;
    @Value("${synth-inntekt-url}")
    private String inntektUrl;
    @Value("${synth-tp-url}")
    private String tpUrl;
    @Value("${synth-tps-url}")
    private String tpsUrl;
    @Value("${synth-frikort-url}")
    private String frikortUrl;
    @Value("${synth-amelding-url}")
    private String aMeldingUrl;



    @PostMapping("/aareg")
    @ApiOperation(value = "Aareg", notes = "Genererer syntetiske arbeidshistorikker bestående av meldinger på AAREG format.")
    @Timed(value = "syntrest.resource.latency", extraTags = {"operation", "synthdata-aareg"})
    public ResponseEntity<List<Arbeidsforholdsmelding>> generateAareg(
            @ApiParam(value = "Liste med identifikasjonsnumre for fikitve personer", required = true)
            @RequestBody List<String> fnrs
    ) {
        InputValidator.validateInput(fnrs);
        List<Arbeidsforholdsmelding> response = (List<Arbeidsforholdsmelding>)
                aaregConsumer.synthesizeData(UriExpander.createRequestEntity(aaregUrl, fnrs), Object.class);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/bisys")
    @ApiOperation(value = "Barnebidragsmelding", notes = "API for å generere syntetiserte bisysmeldinger.")
    @Timed(value = "syntrest.resource.latency", extraTags = {"operation", "synthdata-arena-bisys"})
    public ResponseEntity<List<Barnebidragsmelding>> generateBisys(
            @ApiParam(value = "Antall meldinger som skal genereres", required = true)
            @RequestParam int numToGenerate
    ) {
        InputValidator.validateInput(numToGenerate);
        List<Barnebidragsmelding> response = (List<Barnebidragsmelding>)
                bisysConsumer.synthesizeData(UriExpander.createRequestEntity(bisysUrl, numToGenerate), Object.class);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/inst")
    @ApiOperation(value = "Inst", notes = "Generer et antall institusjonsforholdsmeldinger.")
    @Timed(value = "syntrest.resource.latency", extraTags = {"operation", "synthdata-inst"})
    public ResponseEntity<List<Institusjonsmelding>> generateInst(
            @ApiParam(value = "Antall institusjonsmeldinger", required = true)
            @RequestParam int numToGenerate
    ) {
        InputValidator.validateInput(numToGenerate);
        List<Institusjonsmelding> response = (List<Institusjonsmelding>)
                instConsumer.synthesizeData(UriExpander.createRequestEntity(instUrl, numToGenerate), Object.class);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/medl")
    @ApiOperation(value = "Medl", notes = "Generer MEDL meldinger. For info om selve syntetiseringen og datagrunnlag " +
            "se https://confluence.adeo.no/display/FEL/Syntetisering+-+MEDL\n\nObs! Veldig treg!")
    @Timed(value = "syntrest.resource.latency", extraTags = {"operation", "synthdata-medl"})
    public ResponseEntity<List<Medlemskapsmelding>> generateMedl(
            @ApiParam(value = "Antall meldinger", required = true)
            @RequestParam int numToGenerate
    ) {
        InputValidator.validateInput(numToGenerate);
        List<Medlemskapsmelding> response = (List<Medlemskapsmelding>)
                medlConsumer.synthesizeData(UriExpander.createRequestEntity(medlUrl, numToGenerate), Object.class);
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
        String url = Objects.isNull(arbeidstimer)
                ? arenaMeldekortUrl
                : arenaMeldekortUrl + "?arbeidstimer=" + arbeidstimer;

        List<String> response = (List<String>)
                meldekortConsumer.synthesizeData(UriExpander.createRequestEntity(url, meldegruppe, numToGenerate),
                        Object.class);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/nav/{endringskode}")
    @ApiOperation(value = "Nav Melding", notes = "Opprett et antall meldinger med endringskode fra path variabelen. " +
            "\nReturenterer en liste med strenger der hvert element er en endringsmelding-xml.")
    @Timed(value = "syntrest.resource.latency", extraTags = {"operation", "synthdata-nav"})
    public ResponseEntity<List<String>> generateNavEndringsmelding(
            @ApiParam(value = "Nav endringskode", required = true)
            @PathVariable String endringskode,
            @ApiParam(value = "Antall meldinger", required = true)
            @RequestParam int numToGenerate
    ) {
        InputValidator.validateInput(numToGenerate);
        InputValidator.validateInput(InputValidator.INPUT_STRING_TYPE.ENDRINGSKODE_NAV, endringskode);
        List<String> response = (List<String>)
                navConsumer.synthesizeData(UriExpander.createRequestEntity(navEndringsmeldingUrl, endringskode,
                        numToGenerate), Object.class);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/popp")
    @ApiOperation(value = "Inntektsmeldinger (sigrunstub)", notes = "Genererer syntetiske inntektsmeldinger til Sigrunstub. " +
            "Inntektsmeldingene blir returnert på et format som kan bli lagret i sigrunstub, og vil generere en ny " +
            "inntektsmelding basert på personens inntektsmelding forrige år. Hvis personen ikke har en inntektsmelding " +
            "vil det bli samplet en ny inntektsmelding fra en BeAn/CART-modell.")
    @Timed(value = "syntrest.resource.latency", extraTags = {"operation", "synthdata-popp"})
    public ResponseEntity<List<Inntektsmelding>> generateInntektsmelding(
            @ApiParam(value = "Fnrs å opprette inntektsmeldinger på", required = true)
            @RequestBody List<String> fnrs
    ) {
        InputValidator.validateInput(fnrs);
        List<Inntektsmelding> response = (List<Inntektsmelding>)
                poppConsumer.synthesizeData(UriExpander.createRequestEntity(poppUrl, fnrs),
                        Object.class);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/sam")
    @ApiOperation(value = "Generer SAM melding", notes = "API for å generere syntetiserte SAM data.")
    @Timed(value = "syntrest.resource.latency", extraTags = {"operation", "synthdata-sam"})
    public ResponseEntity<List<SamMelding>> generateSamMelding(
            @ApiParam(value = "Antall meldinger", required = true)
            @RequestParam int numToGenerate
    ) {
        InputValidator.validateInput(numToGenerate);
        List<SamMelding> response = (List<SamMelding>)
                samConsumer.synthesizeData(UriExpander.createRequestEntity(samUrl, numToGenerate),
                        Object.class);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/inntekt")
    @ApiOperation(value = "Inntektsmeldinger (inntektstub)", notes = "Generer inntektsmeldinger på et map med fødselsnumre og " +
            "inntektsmeldinger på samme format som i inntektstub. \n\nHvis man legger ved en liste med inntektsmeldinger " +
            "per fødselsnummer, (altså forrige måneds inntektsmelding) blir den nye inntektsmeldingen basert på disse. " +
            "Hvis man legger ved en tom liste til fødselsnummeret blir en inntektsmelding generert basert på en kernel " +
            "density model.")
    @Timed(value = "syntrest.resource.latency", extraTags = {"operation", "synthdata-inntekt"})
    public ResponseEntity<Map<String, List<no.nav.registre.syntrest.domain.inntekt.Inntektsmelding>>> generateInntektsMelding(
            @ApiParam(value = "Map der key=fødselsnummer, value=liste med inntektsmeldinger", required = true)
            @RequestBody Map<String, List<no.nav.registre.syntrest.domain.inntekt.Inntektsmelding>> fnrInntektMap
    ) {
        InputValidator.validateInput(new ArrayList<>(fnrInntektMap.keySet()));
        Map<String, List<no.nav.registre.syntrest.domain.inntekt.Inntektsmelding>> response =
                (Map<String, List<no.nav.registre.syntrest.domain.inntekt.Inntektsmelding>>)
                        inntektConsumer.synthesizeData(UriExpander.createRequestEntity(inntektUrl, fnrInntektMap),
                                Object.class);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/tp")
    @ApiOperation(value = "Tjeneste Pensjonsmeldinger", notes = "Generer antall tjenestepensjonsmeldinger. For info om " +
            "selve syntetiseringen av TP, se https://confluence.adeo.no/display/FEL/Syntetisering+-+TP")
    @Timed(value = "syntrest.resource.latency", extraTags = {"operation", "synthdata-tp"})
    public ResponseEntity<List<TPmelding>> generateTPMelding(
            @ApiParam(value = "Antall meldinger", required = true)
            @RequestParam int numToGenerate
    ) {
        InputValidator.validateInput(numToGenerate);
        List<TPmelding> response = (List<TPmelding>)
                tpConsumer.synthesizeData(UriExpander.createRequestEntity(tpUrl, numToGenerate), Object.class);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/tps/{endringskode}")
    @ApiOperation(value = "Generer SKD melding", notes = "Lager SKD meldinger for ulike endringskoder")
    @Timed(value = "syntrest.resource.latency", extraTags = {"operation", "synthdata-tps"})
    public ResponseEntity<List<SkdMelding>> generateSkdMelding(
            @ApiParam(value = "Endringskode", required = true)
            @PathVariable String endringskode,
            @ApiParam(value = "Antall meldinger", required = true)
            @RequestParam int numToGenerate
    ) {
        InputValidator.validateInput(InputValidator.INPUT_STRING_TYPE.ENDRINGSKODE, endringskode);
        InputValidator.validateInput(numToGenerate);
        List<SkdMelding> response = (List<SkdMelding>)
                tpsConsumer.synthesizeData(UriExpander.createRequestEntity(tpsUrl, endringskode, numToGenerate),
                        Object.class);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/frikort")
    @ApiOperation(value = "Generer kvitteringer for frikort", notes = "Lager et spesifisert antall kvitteringer for " +
            "hvert personnummersom sendes inn.")
    @Timed(value = "syntrest.resource.latency", extraTags = {"operation", "synthdata-frikort"})
    public ResponseEntity<Map<String, List<FrikortKvittering>>> generateFrikort(
            @ApiParam(value = "Map der key=fødselsnummer og value er antall kvitteringer man ønsker å lage for denne identen.", required = true)
            @RequestBody Map<String, Integer> fnrAntMeldingMap
    ) {
        fnrAntMeldingMap.forEach((key, value) -> InputValidator.validateInput(value));
        Map<String, List<FrikortKvittering>> response = (Map<String, List<FrikortKvittering>>)
                frikortConsumer.synthesizeData(UriExpander.createRequestEntity(frikortUrl, fnrAntMeldingMap),
                        Object.class);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/amelding/arbeidsforhold")
    @Timed(value = "syntrest.resource.latency", extraTags = {"operation", "synthdata-amelding"})
    public ResponseEntity<Arbeidsforhold> generateArbeidforhold(
            @RequestBody Arbeidsforhold tidligereArbeidsforhold
    ) {
        Arbeidsforhold response = (Arbeidsforhold)
                aMeldingConsumer.synthesizeData(UriExpander.createRequestEntity(aMeldingUrl,
                        tidligereArbeidsforhold), Arbeidsforhold.class);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/amelding/arbeidsforhold/sklearn")
    @Timed(value = "syntrest.resource.latency", extraTags = {"operation", "synthdata-amelding"})
    public ResponseEntity<Arbeidsforhold> generateArbeidforholdSklearn(
            @RequestBody Arbeidsforhold tidligereArbeidsforhold
    ) {
        Arbeidsforhold response = (Arbeidsforhold)
                aMeldingConsumer.synthesizeData(UriExpander.createRequestEntity(aMeldingUrl+"/sklearn",
                        tidligereArbeidsforhold), Arbeidsforhold.class);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/amelding/arbeidsforhold/start")
    @Timed(value = "syntrest.resource.latency", extraTags = {"operation", "synthdata-amelding"})
    public ResponseEntity<List<Arbeidsforhold>> generateArbeidforholdStart(
            @RequestBody List<String> startdatoer
    ) {
        List<Arbeidsforhold> response = (List<Arbeidsforhold>)
                aMeldingConsumer.synthesizeData(UriExpander.createRequestEntity(aMeldingUrl+"/start",
                        startdatoer), Object.class);
        doResponseValidation(response);

        return ResponseEntity.ok(response);
    }

    private void doResponseValidation(Object response) {
        if (Objects.isNull(response)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Syntetisering feilet.");
        }
    }
}
