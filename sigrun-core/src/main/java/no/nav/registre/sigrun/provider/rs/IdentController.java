package no.nav.registre.sigrun.provider.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.sigrun.provider.rs.responses.SletteGrunnlagResponse;
import no.nav.registre.sigrun.service.SigrunService;

@RestController
@RequestMapping("api/v1/ident")
public class IdentController {

    @Autowired
    private SigrunService sigrunService;

    @LogExceptions
    @DeleteMapping
    public SletteGrunnlagResponse slettIdenterFraSigrun(
            @RequestHeader(value = "testdataEier") String testdataEier,
            @RequestParam String miljoe,
            @RequestBody List<String> identer) {
        return sigrunService.slettSkattegrunnlagTilIdenter(identer, testdataEier, miljoe);
    }
}
