package no.nav.registre.hodejegeren.provider.rs;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.hodejegeren.consumer.dto.ServiceRoutineDTO;
import no.nav.registre.hodejegeren.domain.Person;
import no.nav.registre.hodejegeren.provider.rs.responses.NavEnhetResponse;
import no.nav.registre.hodejegeren.provider.rs.responses.kontoinfo.KontoinfoResponse;
import no.nav.registre.hodejegeren.provider.rs.responses.persondata.PersondataResponse;
import no.nav.registre.hodejegeren.provider.rs.responses.relasjon.RelasjonsResponse;
import no.nav.registre.hodejegeren.service.CacheService;
import no.nav.registre.hodejegeren.service.EksisterendeIdenterService;
import no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService;

@RestController
public class HodejegerenController {

    public static final int MIN_ALDER = 0;
    public static final int MAX_ALDER = 200;

    @Autowired
    private EksisterendeIdenterService eksisterendeIdenterService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private EndringskodeTilFeltnavnMapperService endringskodeTilFeltnavnMapperService;

    @ApiOperation(value = "Her kan man hente alle identer i en gitt avspillergruppe i TPSF.")
    @GetMapping("api/v1/alle-identer/{avspillergruppeId}")
    public List<String> hentAlleIdenterIGruppe(
            @PathVariable("avspillergruppeId") Long avspillergruppeId
    ) {
        return cacheService.hentAlleIdenterCache(avspillergruppeId);
    }

    @ApiOperation(value = "Her kan man hente alle levende identer i en gitt avspillergruppe i TPSF.")
    @GetMapping("api/v1/alle-levende-identer/{avspillergruppeId}")
    public List<String> hentLevendeIdenterIGruppe(
            @PathVariable("avspillergruppeId") Long avspillergruppeId
    ) {
        return cacheService.hentLevendeIdenterCache(avspillergruppeId);
    }

    @ApiOperation(value = "Her kan man hente alle døde og utvandrede identer fra en gitt avspillergruppe i TPSF.")
    @GetMapping("api/v1/doede-identer/{avspillergruppeId}")
    public List<String> hentDoedeOgUtvandredeIdenterIGruppe(
            @PathVariable("avspillergruppeId") Long avspillergruppeId
    ) {
        return cacheService.hentDoedeOgUtvandredeIdenterCache(avspillergruppeId);
    }

    @ApiOperation(value = "Her kan man hente et gitt antall gifte identer fra en gitt avspillergruppe i TPSF.")
    @GetMapping("api/v1/gifte-identer/{avspillergruppeId}")
    public List<String> hentGifteIdenterIGruppe(
            @PathVariable("avspillergruppeId") Long avspillergruppeId
    ) {
        return cacheService.hentGifteIdenterCache(avspillergruppeId);
    }

    @ApiOperation(value = "Her kan et gitt antall levende identer hentes fra en gitt avspillergruppe i TPSF. "
            + "Systemet sjekker status-quo på personen i det angitte miljø. En minimum alder på personene kan oppgis.")
    @GetMapping("api/v1/levende-identer/{avspillergruppeId}")
    public List<String> hentLevendeIdenter(
            @PathVariable("avspillergruppeId") Long avspillergruppeId,
            @RequestParam("miljoe") String miljoe,
            @RequestParam(value = "antallIdenter", required = false) Integer antallIdenter,
            @RequestParam(value = "minimumAlder", required = false) Integer minimumAlder
    ) {
        if (minimumAlder == null || minimumAlder < MIN_ALDER) {
            minimumAlder = MIN_ALDER;
        }
        return eksisterendeIdenterService
                .hentLevendeIdenter(avspillergruppeId, miljoe, antallIdenter, minimumAlder)
                .collectList()
                .block();
    }

    @ApiOperation(value = "Her kan et gitt antall levende identer hentes fra en gitt avspillergruppe i TPSF. "
            + "Systemet sjekker status-quo på personen i det angitte miljø. En minimum alder på personene kan oppgis.")
    @GetMapping(value = "api/v2/levende-identer/{avspillergruppeId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> hentLevendeIdenterV2(
            @PathVariable("avspillergruppeId") Long avspillergruppeId,
            @RequestParam("miljoe") String miljoe,
            @RequestParam(value = "max", required = false) Integer antallIdenter,
            @RequestParam(value = "minimumAlder", required = false) Integer minimumAlder
    ) {
        if (minimumAlder == null || minimumAlder < MIN_ALDER) {
            minimumAlder = MIN_ALDER;
        }
        return eksisterendeIdenterService
                .hentLevendeIdenter(avspillergruppeId, miljoe, antallIdenter, minimumAlder);
    }

    @ApiOperation(value = "Her kan man hente ut alle levende identer over en viss alder.")
    @GetMapping("api/v1/levende-identer-over-alder/{avspillergruppeId}")
    public List<String> hentAlleLevendeIdenterOverAlder(
            @PathVariable Long avspillergruppeId,
            @RequestParam int minimumAlder
    ) {
        if (minimumAlder < MIN_ALDER) {
            throw new IllegalArgumentException("Minimum alder kan ikke være lavere enn " + MIN_ALDER);
        }
        return eksisterendeIdenterService.finnAlleIdenterOverAlder(avspillergruppeId, minimumAlder);
    }

    @ApiOperation(value = "Her kan man hente ut alle levende identer i en viss aldersgruppe.")
    @GetMapping("api/v1/levende-identer-i-aldersgruppe/{avspillergruppeId}")
    public List<String> hentAlleIdenterIAldersgruppe(
            @PathVariable Long avspillergruppeId,
            @RequestParam int minimumAlder,
            @RequestParam int maksimumAlder
    ) {
        validerAlder(minimumAlder, maksimumAlder);
        return eksisterendeIdenterService.finnLevendeIdenterIAldersgruppe(avspillergruppeId, minimumAlder, maksimumAlder);
    }

    @ApiOperation(value = "Her bestilles et gitt antall identer med tilhørende nav-enhet som hentes tilfeldig fra TPSF. \n"
            + "Disse er føreløpig ikke garantert til å være gyldige fnr med tilhørende arbeidsforhold for å få en sykemelding.\n"
            + "De er garantert til å være myndige.")
    @GetMapping("api/v1/fnr-med-navkontor/{avspillergruppeId}")
    public List<NavEnhetResponse> hentEksisterendeMyndigeIdenterMedNavKontor(
            @PathVariable("avspillergruppeId") Long avspillergruppeId,
            @RequestParam("miljoe") String miljoe,
            @RequestParam("antallIdenter") int antallIdenter
    ) {
        var myndigeIdenter = eksisterendeIdenterService
                .hentLevendeIdenter(avspillergruppeId, miljoe, antallIdenter, 18)
                .collectList()
                .block();
        return eksisterendeIdenterService.hentFnrMedNavKontor(miljoe, myndigeIdenter);
    }

    @ApiOperation(value = "Her kan man sjekke status quo på en ident i TPS.")
    @GetMapping("api/v1/status-quo")
    public Map<String, String> hentStatusQuoFraEndringskode(
            @RequestParam("endringskode") String endringskode,
            @RequestParam("miljoe") String miljoe,
            @RequestParam("fnr") String fnr
    ) throws IOException {
        return new HashMap<>(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(endringskode, miljoe, fnr));
    }

    @ApiOperation(value = "Her kan man hente en liste over identer i en gitt avspillergruppe med tilhørende status-quo "
            + "i et gitt miljø.")
    @GetMapping("api/v1/status-quo-identer/{avspillergruppeId}")
    public Map<String, JsonNode> hentEksisterendeIdenterMedStatusQuo(
            @PathVariable("avspillergruppeId") Long avspillergruppeId,
            @RequestParam("miljoe") String miljoe,
            @RequestParam("antallIdenter") int antallIdenter,
            @RequestParam(value = "minimumAlder", required = false, defaultValue = "" + MIN_ALDER) Integer minimumAlder,
            @RequestParam(value = "maksimumAlder", required = false, defaultValue = "" + MAX_ALDER) Integer maksimumAlder
    ) {
        validerAlder(minimumAlder, maksimumAlder);
        return eksisterendeIdenterService.hentGittAntallIdenterMedStatusQuo(avspillergruppeId, miljoe, antallIdenter, minimumAlder, maksimumAlder);
    }

    @ApiOperation(value = "Her kan man hente navn- og adresseinformasjon til gitte identer i et gitt miljø.")
    @PostMapping("api/v1/adresse-paa-identer")
    public Map<String, JsonNode> hentAdressePaaIdenter(
            @RequestParam String miljoe,
            @RequestBody List<String> identer
    ) {
        return eksisterendeIdenterService.hentAdressePaaIdenter(miljoe, identer);
    }

    @ApiOperation(value = "Her kan man hente alle fødte identer fra en gitt avspillergruppe i TPSF.")
    @GetMapping("api/v1/foedte-identer/{avspillergruppeId}")
    public List<String> hentFoedteIdenter(
            @PathVariable Long avspillergruppeId,
            @RequestParam(required = false, defaultValue = "" + MIN_ALDER) Integer minimumAlder,
            @RequestParam(required = false, defaultValue = "" + MAX_ALDER) Integer maksimumAlder
    ) {
        validerAlder(minimumAlder, maksimumAlder);
        return eksisterendeIdenterService.finnFoedteIdenter(avspillergruppeId, minimumAlder, maksimumAlder);
    }

    @ApiOperation(value = "Her kan man hente persondata til en ident i et gitt miljø i TPS.")
    @GetMapping("api/v1/persondata")
    public PersondataResponse hentPersondataTilIdent(
            @RequestParam String ident,
            @RequestParam String miljoe
    ) {
        return eksisterendeIdenterService.hentPersondata(ident, miljoe);
    }

    @ApiOperation(value = "Her kan man hente identer med norsk kontonummer og tilhørende kjerneinfo")
    @GetMapping("api/v1/identer-med-kontonummer/{avspillergruppeId}")
    public List<KontoinfoResponse> hentPersondataMedKontoinformasjon(
            @PathVariable("avspillergruppeId") Long avspillergruppeId,
            @RequestParam("miljoe") String miljoe,
            @RequestParam("antallIdenter") int antallIdenter,
            @RequestParam(value = "minimumAlder", required = false, defaultValue = "" + MIN_ALDER) Integer minimumAlder,
            @RequestParam(value = "maksimumAlder", required = false, defaultValue = "" + MAX_ALDER) Integer maksimumAlder
    ) {
        validerAlder(minimumAlder, maksimumAlder);
        return eksisterendeIdenterService.hentGittAntallIdenterMedKononummerinfo(avspillergruppeId, miljoe, antallIdenter, minimumAlder, maksimumAlder);
    }

    @ApiOperation(value = "Her kan man hente relasjonene til en ident i et gitt miljø i TPS.")
    @GetMapping("api/v1/relasjoner-til-ident")
    public RelasjonsResponse hentRelasjonerTilIdent(
            @RequestParam String ident,
            @RequestParam String miljoe
    ) {
        return eksisterendeIdenterService.hentRelasjoner(ident, miljoe);
    }

    @ApiOperation(value = "Her kan man hente identer som er i avspillergruppe, men ikke i TPS i gitt miljø.")
    @GetMapping("api/v1/identer-ikke-i-tps/{avspillergruppeId}")
    public List<String> hentIdenterSomIkkeErITps(
            @PathVariable Long avspillergruppeId,
            @RequestParam String miljoe
    ) {
        return eksisterendeIdenterService.hentIdenterSomIkkeErITps(avspillergruppeId, miljoe);
    }

    @ApiOperation(value = "Her kan man hente identer som er i avspillergruppe, og som kolliderer med miljø p i TPS.")
    @GetMapping("api/v1/identer-som-kolliderer/{avspillergruppeId}")
    public List<String> hentIdenterSomKolliderer(
            @PathVariable Long avspillergruppeId
    ) {
        return eksisterendeIdenterService.hentIdenterSomKolliderer(avspillergruppeId);
    }

    @ApiOperation(value = "Her kan man finne ut hvilke identer i en gitt liste, som finnes i en gitt avspillergruppe. Endepunktet returnerer de identene i lista som også finnes i avspillergruppen.")
    @PostMapping("api/v1/filtrerIdenter/{avspillergruppeId}")
    public Set<String> filtrerIdenter(
            @PathVariable Long avspillergruppeId,
            @RequestBody Set<String> identer
    ) {
        Set<String> identerIGruppe = new HashSet<>(cacheService.hentAlleIdenterCache(avspillergruppeId));
        identerIGruppe.retainAll(identer);
        return identerIGruppe;
    }

    private static void validerAlder(
            Integer minimumAlder,
            Integer maksimumAlder
    ) {
        if (minimumAlder < MIN_ALDER || maksimumAlder < minimumAlder) {
            throw new IllegalArgumentException("Minimum alder kan ikke være høyere enn maksimum alder");
        }
    }
}
