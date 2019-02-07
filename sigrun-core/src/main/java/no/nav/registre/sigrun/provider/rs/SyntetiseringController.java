package no.nav.registre.sigrun.provider.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.sigrun.service.PoppService;

@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringController {

    @Autowired
    private PoppService poppService;

    @LogExceptions
    @PostMapping(value = "/generer")
    public ResponseEntity generatePopp(@RequestHeader(value = "testdataEier", defaultValue = "", required = false) String testdataEier,
            @RequestBody List<String> fnrs) {
        return poppService.getPoppMeldinger(fnrs, testdataEier);
    }
}
