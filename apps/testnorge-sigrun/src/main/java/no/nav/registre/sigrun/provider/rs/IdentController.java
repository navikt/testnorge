package no.nav.registre.sigrun.provider.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.sigrun.provider.rs.responses.SletteGrunnlagResponse;
import no.nav.registre.sigrun.service.SigrunService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/ident")
public class IdentController {

    private final SigrunService sigrunService;

    @GetMapping
    public List<String> hentEksisterendeIdenter(
            @RequestParam String miljoe,
            @RequestParam String testdataEier
    ) {
        return sigrunService.finnEksisterendeIdenter(miljoe, testdataEier);
    }

    @DeleteMapping
    public SletteGrunnlagResponse slettIdenterFraSigrun(
            @RequestHeader(value = "testdataEier") String testdataEier,
            @RequestParam String miljoe,
            @RequestBody List<String> identer
    ) {
        return sigrunService.slettSkattegrunnlagTilIdenter(identer, testdataEier, miljoe);
    }
}
