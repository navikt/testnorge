package no.nav.registre.endringsmeldinger.provider.rs;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.transform.TransformerException;
import java.util.List;

import no.nav.registre.endringsmeldinger.consumer.rs.responses.RsPureXmlMessageResponse;
import no.nav.registre.endringsmeldinger.provider.rs.requests.SyntetiserNavEndringsmeldingerRequest;
import no.nav.registre.endringsmeldinger.service.EndringsmeldingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringController {

    private final EndringsmeldingService endringsmeldingService;

    @ApiOperation(value = "Her bestilles generering av syntetiske meldinger for eksisterende identer. Disse meldingene sendes til TPS gjennom TPSF.")
    @PostMapping(value = "/generer")
    public List<RsPureXmlMessageResponse> genererNavMeldinger(
            @RequestBody SyntetiserNavEndringsmeldingerRequest syntetiserNavEndringsmeldingerRequest
    ) throws TransformerException {
        return endringsmeldingService.opprettSyntetiskeNavEndringsmeldinger(syntetiserNavEndringsmeldingerRequest);
    }
}
