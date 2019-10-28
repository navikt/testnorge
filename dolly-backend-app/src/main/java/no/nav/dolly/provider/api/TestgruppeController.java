package no.nav.dolly.provider.api;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;
import static no.nav.dolly.config.CachingConfig.CACHE_TEAM;
import static no.nav.dolly.provider.api.documentation.DocumentationNotes.AAREG_JSON_COMMENT;

import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.service.DollyBestillingService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsDollyBestillingFraIdenterRequest;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestilling;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsOpprettEndreTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppeMedBestillingId;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppeUtvidet;
import no.nav.dolly.domain.testperson.IdentAttributes;
import no.nav.dolly.domain.testperson.IdentAttributesResponse;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.PersonService;
import no.nav.dolly.service.TestgruppeService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v1/gruppe")
public class TestgruppeController {

    private static final String RANDOM_ADRESSE = "For å kunne styre gyldig boadresse basert på kommunenr eller postnummer og med adressesøk i backend: <br />"
            + "\"adresseNrInfo\": { <br />"
            + " &nbsp; \"nummertype\": \"KOMMUNENR/POSTNR\" <br />"
            + " &nbsp; \"nummer\": \"string\", <br />"
            + "} <br /><br />";
    private static final String ADRESSE_COMMON =
            "       &nbsp; &nbsp; \"postnr\": \"string\", <br />"
                    + "     &nbsp; &nbsp; \"kommunenr\": \"string\", <br />"
                    + "     &nbsp; &nbsp; \"flyttedato\": \"string\" <br />";
    public static final String BOADRESSE_COMMENT = "Alternativt har Json-parametere for boadresse følgende parametre: <br />"
            + " For gateadresse:  <br />"
            + "     &nbsp; \"boadresse\": {<br />"
            + "     &nbsp; &nbsp; \"adressetype\": \"GATE\", <br />"
            + "     &nbsp; &nbsp; \"gateadresse\": \"string\", <br />"
            + "     &nbsp; &nbsp; \"husnummer\": \"string\", <br />"
            + "     &nbsp; &nbsp; \"gatekode\": \"string\",<br />"
            + ADRESSE_COMMON + "} <br />"
            + " For matrikkeladresse: <br />"
            + "     &nbsp;   \"boadresse\": {<br />"
            + "     &nbsp; &nbsp; \"adressetype\": \"MATR\", <br />"
            + "     &nbsp; &nbsp; \"mellomnavn\": \"string\", <br />"
            + "     &nbsp; &nbsp; \"gardsnr\": \"string\", <br />"
            + "     &nbsp; &nbsp; \"bruksnr\": \"string\", <br />"
            + "     &nbsp; &nbsp; \"festenr\": \"string\", <br />"
            + "     &nbsp; &nbsp; \"undernr\": \"string\", <br />"
            + ADRESSE_COMMON + "} <br /> <br />";

    public static final String UTEN_ARBEIDSTAKER = "I bestilling benyttes ikke feltet for arbeidstaker. <br /><br />";

    private static final String FULLT_NAVN =
            "     &nbsp; &nbsp; &nbsp; \"etternavn\": \"string\", <br />"
                    + "     &nbsp; &nbsp; &nbsp; \"fornavn\": \"string\", <br />"
                    + "     &nbsp; &nbsp; &nbsp; \"mellomnavn\": \"string\" <br />";

    private static final String ADRESSAT = "&nbsp;   \"adressat\": {<br />";

    private static final String EPILOG = "     &nbsp; } </br /></br />";
    private static final String EPILOG_2 = "     &nbsp; &nbsp; }, </br />";
    private static final String FALSK_IDENTITET_TYPE = "     &nbsp; \"falskIdentitet\": {<br />";

    public static final String KONTAKTINFORMASJON_DOEDSBO = "For kontakinformasjon for dødsbo kan feltet <b>adressat</b> ha en av fire objekttyper: <br />"
            + "For organisasjon eller advokat:<br />"
            + ADRESSAT
            + "     &nbsp; &nbsp; \"adressatType\": \"ORGANISASJON/ADVOKAT\", <br />"
            + "     &nbsp; &nbsp; \"kontaktperson\": { <br />"
            + FULLT_NAVN
            + EPILOG_2
            + "     &nbsp; &nbsp; \"organisajonsnavn\": \"string\", <br />"
            + "     &nbsp; &nbsp; \"organisajonsnummer\": \"string\" <br />"
            + EPILOG
            + "For kontaktperson med ID:<br />"
            + ADRESSAT
            + "     &nbsp; &nbsp; \"adressatType\": \"PERSON_MEDID\", <br />"
            + "     &nbsp; &nbsp; \"idnummer\": \"string\"<br />"
            + EPILOG
            + "For kontaktperson uten ID:<br />"
            + ADRESSAT
            + "     &nbsp; &nbsp; \"adressatType\": \"PERSON_UTENID\", <br />"
            + "     &nbsp; &nbsp; \"navn\": { <br />"
            + FULLT_NAVN
            + EPILOG_2
            + "     &nbsp; &nbsp; \"foedselsdato\": \"string\" <br />"
            + EPILOG;

    public static final String FALSK_IDENTITET = "Falsk identitet inneholder et abstrakt felt, <b>rettIdentitet</b>, som har en av tre objekttyper: <br />"
            + "For identitet ukjent:<br />"
            + FALSK_IDENTITET_TYPE
            + "     &nbsp; &nbsp; \"identitetType\": \"UKJENT\", <br />"
            + "     &nbsp; &nbsp; \"rettIdentitetErUkjent\": true <br />"
            + EPILOG
            + "For identitet med personnummer:<br />"
            + FALSK_IDENTITET_TYPE
            + "     &nbsp; &nbsp; \"identitetType\": \"ENTYDIG\", <br />"
            + "     &nbsp; &nbsp; \"rettIdentitetVedIdentifikasjonsnummer\": \"&lt;fnr/dnr/bost&gt;\" <br />"
            + EPILOG
            + "For identitet med opplysninger:<br />"
            + FALSK_IDENTITET_TYPE
            + "     &nbsp; &nbsp; \"identitetType\": \"OMTRENTLIG\", <br />"
            + "     &nbsp; &nbsp; \"foedselsdato\": \"&lt;dato&gt;\" <br />"
            + "     &nbsp; &nbsp; \"kjoenn\": \"MANN/KVINNE/UBESTEMT\" <br />"
            + "     &nbsp; &nbsp; \"personnavn\":{<br />"
            + FULLT_NAVN
            + EPILOG_2
            + "     &nbsp; &nbsp; \"statsborgerskap\": \"[AUS,GER,FRA,etc]\" <br />"
            + EPILOG;

    private static final String BESTILLING_BESKRIVELSE = RANDOM_ADRESSE + BOADRESSE_COMMENT + AAREG_JSON_COMMENT + UTEN_ARBEIDSTAKER + KONTAKTINFORMASJON_DOEDSBO + FALSK_IDENTITET;

    private final TestgruppeService testgruppeService;
    private final IdentService identService;
    private final MapperFacade mapperFacade;
    private final DollyBestillingService dollyBestillingService;
    private final BestillingService bestillingService;
    private final PersonService personService;

    @CacheEvict(value = CACHE_GRUPPE, allEntries = true)
    @Transactional
    @PutMapping(value = "/{gruppeId}")
    public RsTestgruppeUtvidet oppdaterTestgruppe(@PathVariable("gruppeId") Long gruppeId, @RequestBody RsOpprettEndreTestgruppe testgruppe) {
        Testgruppe gruppe = testgruppeService.oppdaterTestgruppe(gruppeId, testgruppe);
        return mapperFacade.map(gruppe, RsTestgruppeUtvidet.class);
    }

    @CacheEvict(value = { CACHE_GRUPPE, CACHE_TEAM }, allEntries = true)
    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public RsTestgruppeUtvidet opprettTestgruppe(@RequestBody RsOpprettEndreTestgruppe createTestgruppeRequest) {
        Testgruppe gruppe = testgruppeService.opprettTestgruppe(createTestgruppeRequest);
        return mapperFacade.map(testgruppeService.fetchTestgruppeById(gruppe.getId()), RsTestgruppeUtvidet.class);
    }

    @CacheEvict(value = { CACHE_BESTILLING, CACHE_GRUPPE }, allEntries = true)
    @Transactional
    @DeleteMapping("/{gruppeId}/slettTestident")
    public void deleteTestident(@PathVariable Long gruppeId, @RequestParam String ident) {
        if (identService.slettTestident(ident) == 0) {
            throw new NotFoundException(format("Testperson med ident %s ble ikke funnet.", ident));
        }
        bestillingService.slettBestillingByTestIdent(ident);
        personService.recyclePerson(ident);
        personService.releaseArtifacts(singletonList(ident));
    }

    @Cacheable(CACHE_GRUPPE)
    @GetMapping("/{gruppeId}")
    public RsTestgruppeUtvidet getTestgruppe(@PathVariable("gruppeId") Long gruppeId) {
        return mapperFacade.map(testgruppeService.fetchTestgruppeById(gruppeId), RsTestgruppeUtvidet.class);
    }

    @GetMapping("/{gruppeId}/ny")
    public RsTestgruppeMedBestillingId getTestgruppen(@PathVariable("gruppeId") Long gruppeId) {
        return mapperFacade.map(testgruppeService.fetchTestgruppeById(gruppeId), RsTestgruppeMedBestillingId.class);
    }

    @Cacheable(CACHE_GRUPPE)
    @GetMapping
    public List<RsTestgruppe> getTestgrupper(
            @RequestParam(name = "brukerId", required = false) String brukerId) {
        return mapperFacade.mapAsList(testgruppeService.getTestgruppeByBrukerId(brukerId), RsTestgruppe.class);
    }

    @CacheEvict(value = CACHE_GRUPPE, allEntries = true)
    @Transactional
    @DeleteMapping("/{gruppeId}")
    public void slettgruppe(@PathVariable("gruppeId") Long gruppeId) {
        if (testgruppeService.slettGruppeById(gruppeId) == 0) {
            throw new NotFoundException(format("Gruppe med id %s ble ikke funnet.", gruppeId));
        }
    }

    @ApiOperation(value = "Opprett identer i TPS basert på fødselsdato, kjønn og identtype", notes = BESTILLING_BESKRIVELSE)
    @CacheEvict(value = { CACHE_BESTILLING, CACHE_GRUPPE }, allEntries = true)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{gruppeId}/bestilling")
    public RsBestilling opprettIdentBestilling(@PathVariable("gruppeId") Long gruppeId, @RequestBody RsDollyBestillingRequest request) {
        Bestilling bestilling = bestillingService.saveBestilling(gruppeId, request, request.getTpsf(), request.getAntall(), null);

        dollyBestillingService.opprettPersonerByKriterierAsync(gruppeId, request, bestilling);
        return mapperFacade.map(bestilling, RsBestilling.class);
    }

    @ApiOperation(value = "Opprett identer i TPS fra ekisterende identer", notes = BESTILLING_BESKRIVELSE)
    @CacheEvict(value = { CACHE_BESTILLING, CACHE_GRUPPE }, allEntries = true)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{gruppeId}/bestilling/fraidenter")
    public RsBestilling opprettIdentBestillingFraIdenter(@PathVariable("gruppeId") Long gruppeId, @RequestBody RsDollyBestillingFraIdenterRequest request) {
        Bestilling bestilling = bestillingService.saveBestilling(gruppeId, request, request.getTpsf(),
                request.getOpprettFraIdenter().size(), request.getOpprettFraIdenter());

        dollyBestillingService.opprettPersonerFraIdenterMedKriterierAsync(gruppeId, request, bestilling);
        return mapperFacade.map(bestilling, RsBestilling.class);
    }

    @CacheEvict(value = CACHE_GRUPPE, allEntries = true)
    @ApiOperation(value = "Endre status \"i-bruk\" og beskrivelse på testperson")
    @PutMapping("/{gruppeId}/identAttributter")
    @ResponseStatus(HttpStatus.OK)
    public IdentAttributesResponse oppdaterTestident(@PathVariable Long gruppeId, @RequestBody IdentAttributes attributes) {

        return mapperFacade.map(identService.save(attributes), IdentAttributesResponse.class);
    }
}