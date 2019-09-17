package no.nav.registre.hodejegeren.provider.rs;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.hodejegeren.provider.rs.requests.SlettIdenterRequest;
import no.nav.registre.hodejegeren.provider.rs.responses.NavEnhetResponse;
import no.nav.registre.hodejegeren.provider.rs.responses.SlettIdenterResponse;
import no.nav.registre.hodejegeren.provider.rs.responses.persondata.PersondataResponse;
import no.nav.registre.hodejegeren.provider.rs.responses.relasjon.RelasjonsResponse;
import no.nav.registre.hodejegeren.service.EksisterendeIdenterService;
import no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService;

@RestController
public class HodejegerenController {

    public static final int MIN_ALDER = 0;
    public static final int MAX_ALDER = 200;

    @Autowired
    private EksisterendeIdenterService eksisterendeIdenterService;

    @Autowired
    private EndringskodeTilFeltnavnMapperService endringskodeTilFeltnavnMapperService;

    @LogExceptions
    @ApiOperation(value = "Her kan man hente alle identer i en gitt avspillergruppe i TPSF.")
    @GetMapping("api/v1/alle-identer/{avspillergruppeId}")
    public List<String> hentAlleIdenterIGruppe(@PathVariable("avspillergruppeId") Long avspillergruppeId) {
        return eksisterendeIdenterService.finnAlleIdenter(avspillergruppeId);
    }

    @LogExceptions
    @ApiOperation(value = "Her kan man hente alle levende identer i en gitt avspillergruppe i TPSF.")
    @GetMapping("api/v1/alle-levende-identer/{avspillergruppeId}")
    public List<String> hentLevendeIdenterIGruppe(@PathVariable("avspillergruppeId") Long avspillergruppeId) {
        return eksisterendeIdenterService.finnLevendeIdenter(avspillergruppeId);
    }

    @LogExceptions
    @ApiOperation(value = "Her kan man hente alle døde og utvandrede identer fra en gitt avspillergruppe i TPSF.")
    @GetMapping("api/v1/doede-identer/{avspillergruppeId}")
    public List<String> hentDoedeOgUtvandredeIdenterIGruppe(@PathVariable("avspillergruppeId") Long avspillergruppeId) {
        return eksisterendeIdenterService.finnDoedeOgUtvandredeIdenter(avspillergruppeId);
    }

    @LogExceptions
    @ApiOperation(value = "Her kan man hente et gitt antall gifte identer fra en gitt avspillergruppe i TPSF.")
    @GetMapping("api/v1/gifte-identer/{avspillergruppeId}")
    public List<String> hentGifteIdenterIGruppe(@PathVariable("avspillergruppeId") Long avspillergruppeId) {
        return eksisterendeIdenterService.finnGifteIdenter(avspillergruppeId);
    }

    @LogExceptions
    @ApiOperation(value = "Her kan et gitt antall levende identer hentes fra en gitt avspillergruppe i TPSF. "
            + "Systemet sjekker status-quo på personen i det angitte miljø. En minimum alder på personene kan oppgis.")
    @GetMapping("api/v1/levende-identer/{avspillergruppeId}")
    public List<String> hentLevendeIdenter(@PathVariable("avspillergruppeId") Long avspillergruppeId, @RequestParam("miljoe") String miljoe,
            @RequestParam(value = "antallIdenter", required = false) Integer antallIdenter, @RequestParam(value = "minimumAlder", required = false) Integer minimumAlder) {
        if (minimumAlder == null || minimumAlder < MIN_ALDER) {
            minimumAlder = MIN_ALDER;
        }
        return eksisterendeIdenterService.hentLevendeIdenterIGruppeOgSjekkStatusQuo(avspillergruppeId, miljoe, antallIdenter, minimumAlder);
    }

    @LogExceptions
    @ApiOperation(value = "Her kan man hente ut alle levende identer over en viss alder.")
    @GetMapping("api/v1/levende-identer-over-alder/{avspillergruppeId}")
    public List<String> hentAlleLevendeIdenterOverAlder(@PathVariable Long avspillergruppeId, @RequestParam int minimumAlder, HttpServletResponse response) {
        if (minimumAlder < MIN_ALDER) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return new ArrayList<>();
        }
        return eksisterendeIdenterService.finnAlleIdenterOverAlder(avspillergruppeId, minimumAlder);
    }

    @LogExceptions
    @ApiOperation(value = "Her kan man hente ut alle levende identer i en viss aldersgruppe.")
    @GetMapping("api/v1/levende-identer-i-aldersgruppe/{avspillergruppeId}")
    public List<String> hentAlleIdenterIAldersgruppe(@PathVariable Long avspillergruppeId, @RequestParam int minimumAlder, @RequestParam int maksimumAlder, HttpServletResponse response) {
        if (minimumAlder < MIN_ALDER || maksimumAlder < minimumAlder) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return new ArrayList<>();
        }
        return eksisterendeIdenterService.finnLevendeIdenterIAldersgruppe(avspillergruppeId, minimumAlder, maksimumAlder);
    }

    @LogExceptions
    @ApiOperation(value = "Her bestilles et gitt antall identer med tilhørende nav-enhet som hentes tilfeldig fra TPSF. \n"
            + "Disse er føreløpig ikke garantert til å være gyldige fnr med tilhørende arbeidsforhold for å få en sykemelding.\n"
            + "De er garantert til å være myndige.")
    @GetMapping("api/v1/fnr-med-navkontor/{avspillergruppeId}")
    public List<NavEnhetResponse> hentEksisterendeMyndigeIdenterMedNavKontor(@PathVariable("avspillergruppeId") Long avspillergruppeId, @RequestParam("miljoe") String miljoe,
            @RequestParam("antallIdenter") int antallIdenter) {
        List<String> myndigeIdenter = eksisterendeIdenterService.hentLevendeIdenterIGruppeOgSjekkStatusQuo(avspillergruppeId, miljoe, antallIdenter, 18);
        return eksisterendeIdenterService.hentFnrMedNavKontor(miljoe, myndigeIdenter);
    }

    @LogExceptions
    @ApiOperation(value = "Her kan man sjekke status quo på en ident i TPS.")
    @GetMapping("api/v1/status-quo")
    public Map<String, String> hentStatusQuoFraEndringskode(@RequestParam("endringskode") String endringskode, @RequestParam("miljoe") String miljoe, @RequestParam("fnr") String fnr) throws IOException {
        return new HashMap<>(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(endringskode, miljoe, fnr));
    }

    @LogExceptions
    @ApiOperation(value = "Her kan man sende inn en liste med identer og hodejegeren vil slette alle som ikke ligger i TPS (de som mangler status-quo-informasjon).")
    @DeleteMapping("api/v1/status-quo-slett")
    public SlettIdenterResponse slettIdenterUtenStatusQuo(@RequestParam("avspillergruppeId") Long avspillergruppeId, @RequestParam("miljoe") String miljoe, @RequestBody SlettIdenterRequest slettIdenterRequest)
            throws IOException {
        return eksisterendeIdenterService.slettIdenterUtenStatusQuo(avspillergruppeId, miljoe, slettIdenterRequest.getIdenterSomSkalSlettes());
    }

    @LogExceptions
    @ApiOperation(value = "Her kan man hente en liste over identer i en gitt avspillergruppe med tilhørende status-quo "
            + "i et gitt miljø.")
    @GetMapping("api/v1/status-quo-identer/{avspillergruppeId}")
    public Map<String, JsonNode> hentEksisterendeIdenterMedStatusQuo(@PathVariable("avspillergruppeId") Long avspillergruppeId,
            @RequestParam("miljoe") String miljoe,
            @RequestParam("antallIdenter") int antallIdenter,
            @RequestParam(value = "minimumAlder", required = false) Integer minimumAlder,
            @RequestParam(value = "maksimumAlder", required = false) Integer maksimumAlder,
            HttpServletResponse response) {
        if (minimumAlder == null) {
            minimumAlder = MIN_ALDER;
        }
        if (maksimumAlder == null) {
            maksimumAlder = MAX_ALDER;
        }
        if (minimumAlder < MIN_ALDER || maksimumAlder < minimumAlder) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return new HashMap<>();
        }
        return eksisterendeIdenterService.hentGittAntallIdenterMedStatusQuo(avspillergruppeId, miljoe, antallIdenter, minimumAlder, maksimumAlder);
    }

    @LogExceptions
    @ApiOperation(value = "Her kan man hente navn- og adresseinformasjon til gitte identer i et gitt miljø.")
    @PostMapping("api/v1/adresse-paa-identer")
    public Map<String, JsonNode> hentAdressePaaIdenter(@RequestParam String miljoe, @RequestBody List<String> identer) {
        return eksisterendeIdenterService.hentAdressePaaIdenter(miljoe, identer);
    }

    @LogExceptions
    @ApiOperation(value = "Her kan man hente alle fødte identer fra en gitt avspillergruppe i TPSF.")
    @GetMapping("api/v1/foedte-identer/{avspillergruppeId}")
    public List<String> hentFoedteIdenter(@PathVariable Long avspillergruppeId) {
        return eksisterendeIdenterService.finnFoedteIdenter(avspillergruppeId);
    }

    @LogExceptions
    @ApiOperation(value = "Her kan man hente persondata til en ident i et gitt miljø i TPS.")
    @GetMapping("api/v1/persondata")
    public PersondataResponse hentPersondataTilIdent(@RequestParam String ident, @RequestParam String miljoe) {
        return eksisterendeIdenterService.hentPersondata(ident, miljoe);
    }

    @LogExceptions
    @ApiOperation(value = "Her kan man hente relasjonene til en ident i et gitt miljø i TPS.")
    @GetMapping("api/v1/relasjoner-til-ident")
    public RelasjonsResponse hentRelasjonerTilIdent(@RequestParam String ident, @RequestParam String miljoe) {
        return eksisterendeIdenterService.hentRelasjoner(ident, miljoe);
    }
}
