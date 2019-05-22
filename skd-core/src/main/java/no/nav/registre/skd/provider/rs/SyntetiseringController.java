package no.nav.registre.skd.provider.rs;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.skd.consumer.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.skd.provider.rs.requests.FastMeldingRequest;
import no.nav.registre.skd.provider.rs.requests.GenereringsOrdreRequest;
import no.nav.registre.skd.service.FasteMeldingerService;
import no.nav.registre.skd.service.HodejegerDatabaseService;
import no.nav.registre.skd.service.SyntetiseringService;

@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringController {

    @Autowired
    private SyntetiseringService syntetiseringService;

    @Autowired
    private HodejegerDatabaseService hodejegerDatabaseService;

    @Autowired
    private FasteMeldingerService fasteMeldingerService;

    @LogExceptions
    @ApiOperation(value = "Her bestilles genererering av syntetiske meldinger for nye og eksisterende identer. "
            + "Disse meldingene lagres i angitt gruppe i TPSF. ", notes = "Eksisterende identer hentes fra avspillergruppen og status quo på disse hentes fra TPS i angitt miljø. " +
            "\n\nSpesialbehandlinger: \n\n" +
            "\tVed bestilling av endringskode 0211, genereres tilhørende endringsmelding 3510.\n\n" +
            "\tVed bestilling av endringskode 1110, genereres tilhørende endringsmelding 1110 til partner.\n\n" +
            "\tVed bestilling av endringskode 1410, genereres tilhørende endringsmelding 1410 til partner.\n\n" +
            "\tVed bestilling av endringskode 1810, genereres tilhørende endringsmelding 1810 til partner.\n\n" +
            "\tVed bestilling av endringskode 4310, genereres tilhørende endringsmelding 8510 på en eventuell partner.")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "De opprettede skdmeldingene ble lagret på disse id-ene i TPSF") })
    @PostMapping(value = "/generer")
    public ResponseEntity genererSkdMeldinger(@RequestBody GenereringsOrdreRequest genereringsOrdreRequest) {
        ResponseEntity genererSkdMeldingerResponse = syntetiseringService.puttIdenterIMeldingerOgLagre(genereringsOrdreRequest);
        hodejegerDatabaseService.sendIdenterMedSkdMeldingerTilHodejegeren(syntetiseringService.getIdenterMedSkdMeldinger());
        return genererSkdMeldingerResponse;
    }

    @LogExceptions
    @ApiOperation(value = "Her kan man starte avspilling av en TPSF-avspillergruppe. Dette innebærer at alle skd-meldingene i en gitt gruppe sendes til TPS i et gitt miljø.")
    @PostMapping(value = "/startAvspilling/{avspillergruppeId}")
    public SkdMeldingerTilTpsRespons startAvspillingAvTpsfAvspillergruppe(@PathVariable Long avspillergruppeId, @RequestParam String miljoe) {
        return fasteMeldingerService.startAvspillingAvTpsfAvspillergruppe(avspillergruppeId, miljoe);
    }

    @LogExceptions
    @ApiOperation(value = "Her kan man legge nye identer inn i en gitt avspillergruppe i TPSF. Identene vil få opprettet en innvandringsmelding hver.")
    @PostMapping(value = "/leggTilNyeMeldinger/{avspillergruppeId}")
    public List<Long> leggTilNyeMeldingerIGruppe(@PathVariable Long avspillergruppeId, @RequestBody List<FastMeldingRequest> fasteMeldinger) {
        return fasteMeldingerService.opprettMeldingerOgLeggIGruppe(avspillergruppeId, fasteMeldinger);
    }
}
