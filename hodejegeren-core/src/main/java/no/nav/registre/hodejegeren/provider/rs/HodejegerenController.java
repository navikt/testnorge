package no.nav.registre.hodejegeren.provider.rs;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import no.nav.registre.hodejegeren.provider.rs.responses.relasjon.RelasjonsResponse;
import no.nav.registre.hodejegeren.service.EksisterendeIdenterService;
import no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService;
import no.nav.registre.hodejegeren.service.Endringskoder;

@RestController
public class HodejegerenController {

    @Autowired
    private EksisterendeIdenterService eksisterendeIdenterService;

    @Autowired
    private EndringskodeTilFeltnavnMapperService endringskodeTilFeltnavnMapperService;

    @LogExceptions
    @ApiOperation(value = "Her kan man hente alle personer i en gitt avspillergruppe i TPSF.")
    @GetMapping("api/v1/alle-identer/{avspillergruppeId}")
    public List<String> hentAlleIdenterIGruppe(@PathVariable("avspillergruppeId") Long avspillergruppeId) {
        return eksisterendeIdenterService.finnAlleIdenter(avspillergruppeId);
    }

    @LogExceptions
    @ApiOperation(value = "Her kan man hente alle levende personer i en gitt avspillergruppe i TPSF.")
    @GetMapping("api/v1/alle-levende-identer/{avspillergruppeId}")
    public List<String> hentLevendeIdenterIGruppe(@PathVariable("avspillergruppeId") Long avspillergruppeId) {
        return eksisterendeIdenterService.finnLevendeIdenter(avspillergruppeId);
    }

    @LogExceptions
    @ApiOperation(value = "Her kan man hente alle døde og utvandrede personer fra en gitt avspillergruppe i TPSF.")
    @GetMapping("api/v1/doede-identer/{avspillergruppeId}")
    public List<String> hentDoedeOgUtvandredeIdenterIGruppe(@PathVariable("avspillergruppeId") Long avspillergruppeId) {
        return eksisterendeIdenterService.finnDoedeOgUtvandredeIdenter(avspillergruppeId);
    }

    @LogExceptions
    @ApiOperation(value = "Her kan man hente et gitt antall gifte personer fra en gitt avspillergruppe i TPSF.")
    @GetMapping("api/v1/gifte-identer/{avspillergruppeId}")
    public List<String> hentGifteIdenterIGruppe(@PathVariable("avspillergruppeId") Long avspillergruppeId) {
        return eksisterendeIdenterService.finnGifteIdenter(avspillergruppeId);
    }

    @LogExceptions
    @ApiOperation(value = "Her kan et gitt antall levende personer hentes fra en gitt avspillergruppe i TPSF. "
            + "Systemet sjekker status-quo på personen i det angitte miljø. En minimum alder på personene kan oppgis.")
    @GetMapping("api/v1/levende-identer/{avspillergruppeId}")
    public List<String> hentLevendeIdenter(@PathVariable("avspillergruppeId") Long avspillergruppeId, @RequestParam("miljoe") String miljoe,
            @RequestParam(value = "antallPersoner", required = false) Integer antallPersoner, @RequestParam(value = "minimumAlder", required = false) Integer minimumAlder) {
        if (minimumAlder == null || minimumAlder < 0) {
            minimumAlder = 0;
        }
        if (antallPersoner == null || antallPersoner < 0) {
            antallPersoner = Integer.MAX_VALUE;
        }

        return eksisterendeIdenterService.hentLevendeIdenterIGruppeOgSjekkStatusQuo(avspillergruppeId, miljoe, antallPersoner, minimumAlder);
    }

    @LogExceptions
    @ApiOperation(value = "Her kan man hente ut alle levende identer over en viss alder.")
    @GetMapping("api/v1/levende-identer-over-alder/{avspillergruppeId}")
    public List<String> hentAlleLevendeIdenterOverAlder(@PathVariable Long avspillergruppeId, @RequestParam int minimumAlder, HttpServletResponse response) {
        if (minimumAlder < 0) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return new ArrayList<>();
        }
        return eksisterendeIdenterService.finnAlleIdenterOverAlder(avspillergruppeId, minimumAlder);
    }

    @LogExceptions
    @ApiOperation(value = "Her kan man hente ut alle levende identer i en viss aldersgruppe.")
    @GetMapping("api/v1/levende-identer-i-aldersgruppe/{avspillergruppeId}")
    public List<String> hentAlleIdenterIAldersgruppe(@PathVariable Long avspillergruppeId, @RequestParam int minimumAlder, @RequestParam int maksimumAlder, HttpServletResponse response) {
        if (minimumAlder < 0 || maksimumAlder < 0) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return new ArrayList<>();
        }
        return eksisterendeIdenterService.finnAlleIdenterIAldersgruppe(avspillergruppeId, minimumAlder, maksimumAlder);
    }

    @LogExceptions
    @ApiOperation(value = "Her bestilles et gitt antall identer med tilhørende nav-enhet som hentes tilfeldig fra TPSF. \n"
            + "Disse er føreløpig ikke garantert til å være gyldige fnr med tilhørende arbeidsforhold for å få en sykemelding.\n"
            + "De er garantert til å være myndige.")
    @GetMapping("api/v1/fnr-med-navkontor/{avspillergruppeId}")
    public Map<String, String> hentEksisterendeMyndigeIdenterMedNavKontor(@PathVariable("avspillergruppeId") Long avspillergruppeId, @RequestParam("miljoe") String miljoe,
            @RequestParam("antallPersoner") int antallPersoner) {
        List<String> myndigeIdenter = eksisterendeIdenterService.hentLevendeIdenterIGruppeOgSjekkStatusQuo(avspillergruppeId, miljoe, antallPersoner, 18);
        return eksisterendeIdenterService.hentFnrMedNavKontor(miljoe, myndigeIdenter);
    }

    @LogExceptions
    @ApiOperation(value = "Her kan man sjekke status quo på en ident i TPS.")
    @GetMapping("api/v1/status-quo")
    public Map<String, String> hentStatusQuoFraEndringskode(@RequestParam("endringskode") Endringskoder endringskode, @RequestParam("miljoe") String miljoe, @RequestParam("fnr") String fnr) throws IOException {
        return new HashMap<>(endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(endringskode, miljoe, fnr));
    }

    @LogExceptions
    @ApiOperation(value = "Her kan man hente listen over identer i en gitt avspillergruppe med tilhørende status-quo "
            + "i et gitt miljø.")
    @GetMapping("api/v1/status-quo-identer/{avspillergruppeId}")
    public Map<String, JsonNode> hentEksisterendeIdenterMedStatusQuo(@PathVariable("avspillergruppeId") Long avspillergruppeId,
            @RequestParam("miljoe") String miljoe, @RequestParam("antallPersoner") int antallPersoner) {
        return eksisterendeIdenterService.hentGittAntallIdenterMedStatusQuo(avspillergruppeId, miljoe, antallPersoner);
    }

    @LogExceptions
    @ApiOperation(value = "Her kan man hente navn- og adresseinformasjon til gitte identer i et gitt miljø.")
    @PostMapping("api/v1/adresse-paa-identer")
    public Map<String, JsonNode> hentAdressePaaIdenter(@RequestParam String miljoe, @RequestBody List<String> identer) {
        return eksisterendeIdenterService.hentAdressePaaIdenter(miljoe, identer);
    }

    @LogExceptions
    @ApiOperation(value = "Her kan man hente alle fødte personer fra en gitt avspillergruppe i TPSF.")
    @GetMapping("api/v1/foedte-identer/{avspillergruppeId}")
    public List<String> hentFoedteIdenter(@PathVariable Long avspillergruppeId) {
        return eksisterendeIdenterService.finnFoedteIdenter(avspillergruppeId);
    }

    @LogExceptions
    @ApiOperation(value = "Her kan man hente relasjonene til en ident i et gitt miljø i TPS.")
    @GetMapping("api/v1/relasjoner-til-ident")
    public RelasjonsResponse hentRelasjonerTilIdent(@RequestParam String ident, @RequestParam String miljoe) {
        return eksisterendeIdenterService.hentRelasjoner(ident, miljoe);
    }
}
