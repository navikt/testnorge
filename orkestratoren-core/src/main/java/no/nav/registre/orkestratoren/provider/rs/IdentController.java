package no.nav.registre.orkestratoren.provider.rs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.orkestratoren.provider.rs.responses.SlettedeIdenterResponse;
import no.nav.registre.orkestratoren.service.IdentService;

@RestController
@RequestMapping("api/v1/ident")
public class IdentController {

    @Autowired
    private IdentService identService;

    @LogExceptions
    @DeleteMapping
    public SlettedeIdenterResponse slettIdenterFraAdaptere(
            @RequestParam Long avspillergruppeId,
            @RequestParam String miljoe,
            @RequestParam String testdataEier,
            @RequestBody List<String> identer) {
        return identService.slettIdenterFraAdaptere(avspillergruppeId, miljoe, testdataEier, identer);
    }
}
