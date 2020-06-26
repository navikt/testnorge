package no.nav.registre.frikort.provider.rs;

import static no.nav.registre.frikort.utils.SwaggerUtils.LEGG_PAA_KOE_DESCRIPTION;
import static no.nav.registre.frikort.utils.SwaggerUtils.REQUEST_BODY_DESCRIPTION;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBException;
import java.util.List;

import no.nav.registre.frikort.provider.rs.request.SyntetiserFrikortRequest;
import no.nav.registre.frikort.provider.rs.response.SyntetiserFrikortResponse;
import no.nav.registre.frikort.service.SyntetiseringService;

@Slf4j
@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class SyntetiseringController {

    private final SyntetiseringService syntetiseringService;

    @PostMapping(value = "/generer")
    @ApiOperation(value = "Generer syntetiske egenandelsmeldinger som XML string på identer i gitt avspillergruppe fra gitt miljø.")
    public ResponseEntity<List<SyntetiserFrikortResponse>> genererEgenandelsmeldinger(
            @ApiParam(value = REQUEST_BODY_DESCRIPTION, required = true)
            @RequestBody() SyntetiserFrikortRequest syntetiserFrikortRequest,
            @ApiParam(value = LEGG_PAA_KOE_DESCRIPTION)
            @RequestParam(defaultValue = "true") boolean leggPaaKoe
    ) throws JAXBException {
        return ResponseEntity.ok(syntetiseringService.opprettSyntetiskeFrikort(syntetiserFrikortRequest, leggPaaKoe));
    }
}
