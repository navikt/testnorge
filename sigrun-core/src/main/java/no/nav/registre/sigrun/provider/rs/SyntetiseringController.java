package no.nav.registre.sigrun.provider.rs;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.sigrun.provider.rs.requests.SyntetiserPoppRequest;
import no.nav.registre.sigrun.service.SigrunService;

@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringController {

    @Autowired
    private SigrunService sigrunService;

    @ApiOperation(value = "Start syntetisering av personsopptjeningsmeldinger")
    @LogExceptions
    @PostMapping(value = "/generer")
    public ResponseEntity startSyntetisering(
            @RequestHeader(value = "testdataEier", defaultValue = "", required = false) String testdataEier,
            @RequestBody SyntetiserPoppRequest syntetiserPoppRequest
    ) {
        var identer = sigrunService.finnEksisterendeOgNyeIdenter(syntetiserPoppRequest, testdataEier);
        return sigrunService.genererPoppmeldingerOgSendTilSigrunStub(identer, testdataEier, syntetiserPoppRequest.getMiljoe());
    }
}
