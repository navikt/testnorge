package no.nav.registre.frikort.provider.rs;

import static no.nav.registre.frikort.utils.SwaggerUtils.LEGG_PAA_KOE_DESCRIPTION;
import static no.nav.registre.frikort.utils.SwaggerUtils.VALIDER_EGENANDEL_DESCRIPTION;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.frikort.consumer.rs.response.SyntFrikortResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;

import no.nav.registre.frikort.consumer.rs.response.SyntFrikortResponse;
import no.nav.registre.frikort.provider.rs.request.EgenandelRequest;
import no.nav.registre.frikort.provider.rs.request.IdentRequest;
import no.nav.registre.frikort.provider.rs.response.SyntetiserFrikortResponse;
import no.nav.registre.frikort.service.IdentService;

@Slf4j
@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class IdentController {

    private final IdentService identService;

    @PostMapping(value = "/ident/syntetisk")
    @ApiOperation(value = "Generer syntetiske egenandelsmeldinger som XML string på gitte identer.")
    public ResponseEntity<List<SyntetiserFrikortResponse>> genererEgenandelsmeldinger(
            @RequestBody() IdentRequest identRequest,
            @ApiParam(value = LEGG_PAA_KOE_DESCRIPTION)
            @RequestParam(defaultValue = "true") boolean leggPaaKoe,
            @ApiParam(value = VALIDER_EGENANDEL_DESCRIPTION)
            @RequestParam(defaultValue = "true", required = false) boolean validerEgenandeler
    ) throws JAXBException {
        return ResponseEntity.ok(identService.opprettSyntetiskeEgenandeler(identRequest, leggPaaKoe, validerEgenandeler));
    }

    @PostMapping(value = "/ident")
    @ApiOperation(value = "Generer egenandelsmeldinger som XML og legg på kø.")
    public ResponseEntity<List<SyntetiserFrikortResponse>> opprettEgenandelsmeldinger(
            @RequestBody() List<EgenandelRequest> egenandeler,
            @ApiParam(value = LEGG_PAA_KOE_DESCRIPTION)
            @RequestParam(defaultValue = "true") boolean leggPaaKoe
    ) throws JAXBException {
        return ResponseEntity.ok(identService.opprettEgenandeler(egenandeler, leggPaaKoe));
    }

    @GetMapping("/syntetiskData")
    @ApiOperation(value = "Her kan man hente syntetiske egenandeler på json-format.")
    public ResponseEntity<List<SyntFrikortResponseDTO>> hentSyntetiskeEgenandeler(
            @RequestParam Integer antallEgenandeler,
            @ApiParam(value = VALIDER_EGENANDEL_DESCRIPTION)
            @RequestParam(defaultValue = "true", required = false) boolean validerEgenandeler
    ) {
        var identerMedEgenandeler = identService.hentSyntetiskeEgenandeler(antallEgenandeler, validerEgenandeler);
        var syntetiskeEgenandeler = new ArrayList<SyntFrikortResponseDTO>();
        identerMedEgenandeler.forEach((key, value) -> syntetiskeEgenandeler.addAll(value));
        return ResponseEntity.ok(syntetiskeEgenandeler);
    }

    @GetMapping("/miljoer")
    @ApiOperation(value = "Her kan man sjekke hvilke miljøer frikort er tilgjengelig i.")
    public ResponseEntity<List<String>> hentTilgjengeligeMiljoer() {
        return ResponseEntity.ok(identService.hentTilgjengeligeMiljoer());
    }
}
