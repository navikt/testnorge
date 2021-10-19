package no.nav.registre.sigrun.provider.rs;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import no.nav.registre.sigrun.provider.rs.requests.SyntetiserSigrunRequest;
import no.nav.registre.sigrun.service.SigrunService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringController {

    private final SigrunService sigrunService;

    @ApiOperation(value = "Start syntetisering av personsopptjeningsmeldinger")
    @PostMapping(value = "/generer")
    public ResponseEntity<List<String>> startSyntetisering(
            @RequestHeader(value = "testdataEier", defaultValue = "", required = false) String testdataEier,
            @RequestBody SyntetiserSigrunRequest syntetiserSigrunRequest
    ) {
        var identer = sigrunService.finnEksisterendeOgNyeIdenter(syntetiserSigrunRequest, testdataEier);
        return sigrunService.genererPoppmeldingerOgSendTilSigrunStub(identer, testdataEier, syntetiserSigrunRequest.getMiljoe());
    }
}
