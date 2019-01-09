package no.nav.registre.hodejegeren.provider.rs;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.hodejegeren.provider.rs.requests.GenereringsOrdreRequest;
import no.nav.registre.hodejegeren.service.HodejegerService;

@RestController
public class TriggeSyntetiseringController {

    @Autowired
    private HodejegerService hodejegerService;

    @LogExceptions
    @ApiOperation(value = "Her bestilles genererering av syntetiske meldinger for nye og eksisterende identer. "
            + "Disse meldingene lagres i angitt gruppe i TPSF. ", notes = "Eksisterende identer hentes fra avspillergruppen og status quo på disse hentes fra TPS i angitt miljø. " +
            "\n\nSpesialbehandlinger: \n\n" +
            "\tVed bestilling av endringskode 0211, genereres tilhørende endringsmelding 3510.\n\n" +
            "\tVed bestilling av endringskode 1110, genereres tilhørende endringsmelding 1110 til partner.\n\n" +
            "\tVed bestilling av endringskode 1410, genereres tilhørende endringsmelding 1410 til partner.\n\n" +
            "\tVed bestilling av endringskode 1810, genereres tilhørende endringsmelding 1810 til partner.")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "De opprettede skdmeldingene ble lagret på disse id-ene i TPSF") })
    @PostMapping("api/v1/syntetisering/generer")
    public ResponseEntity genererSyntetiskeMeldingerOgLagreITpsf(@RequestBody GenereringsOrdreRequest ordreRequest) {
        return hodejegerService.puttIdenterIMeldingerOgLagre(ordreRequest);
    }
}
