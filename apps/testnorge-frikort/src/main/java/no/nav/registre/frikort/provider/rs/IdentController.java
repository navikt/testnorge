package no.nav.registre.frikort.provider.rs;

import static no.nav.registre.frikort.utils.SwaggerUtils.LEGG_PAA_KOE_DESCRIPTION;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBException;
import java.util.List;

import no.nav.registre.frikort.provider.rs.request.IdentRequest;
import no.nav.registre.frikort.provider.rs.response.SyntetiserFrikortResponse;
import no.nav.registre.frikort.service.IdentService;

@Slf4j
@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class IdentController {

    private final IdentService identService;

    @PostMapping(value = "/ident/opprett")
    @ApiOperation(value = "Generer syntetiske egenandelsmeldinger som XML string på gitte identer.")
    public ResponseEntity<List<SyntetiserFrikortResponse>> genererEgenandelsmeldinger(
            @RequestBody() IdentRequest identRequest,
            @ApiParam(value = LEGG_PAA_KOE_DESCRIPTION)
            @RequestParam(defaultValue = "true") boolean leggPaaKoe
    ) throws JAXBException {
        return ResponseEntity.ok(identService.opprettSyntetiskeEgenandeler(identRequest, leggPaaKoe));
    }

    @GetMapping("/miljoer")
    @ApiOperation(value = "Her kan man sjekke hvilke miljøer frikort er tilgjengelig i.")
    public List<String> hentTilgjengeligeMiljoer() {
        return identService.hentTilgjengeligeMiljoer();
    }
}
