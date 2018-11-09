package no.nav.registre.hodejegeren.provider.rs;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.hodejegeren.provider.rs.requests.GenereringsOrdreRequest;
import no.nav.registre.hodejegeren.service.HodejegerService;

@RestController
public class TriggeSyntetiseringController {
    
    @Autowired
    private HodejegerService hodejegerService;
    
    @LogExceptions
    @ApiOperation(value = "Her bestilles genererering av syntetiske meldinger for nye og eksisterende identer. "
            + "Disse meldingene lagres i angitt gruppe i TPSF. ",
            notes = "Eksisterende identer hentes fra avspillergruppen og status quo på disse hentes fra TPS i angitt miljø. ")
    @PostMapping("api/v1/syntetisering/generer")
    public @ResponseStatus(HttpStatus.CREATED) List<Long> genererSyntetiskeMeldingerOgLagreITpsf(@RequestBody GenereringsOrdreRequest ordreRequest) {
        return hodejegerService.puttIdenterIMeldingerOgLagre(ordreRequest);
    }
}
