package no.nav.registre.endringsmeldinger.provider.rs;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.transform.TransformerException;
import java.util.List;

import no.nav.registre.endringsmeldinger.consumer.rs.responses.RsPureXmlMessageResponse;
import no.nav.registre.endringsmeldinger.provider.rs.requests.GenererKontonummerRequest;
import no.nav.registre.endringsmeldinger.service.TestdataService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/testdata")
public class TestdataController {

    private final TestdataService testdataService;

    @ApiOperation(value = "Her opprettes det meldinger for endring av norsk gironummer på en liste med identer. Alle identer får det gitte gironummeret, og endringsmeldingene sendes til angitt kø i det angitte miljøet.")
    @PostMapping(value = "/genererKontonummer")
    public List<RsPureXmlMessageResponse> genererKontonummerPaaIdenter(
            @RequestParam String koeNavn,
            @RequestBody GenererKontonummerRequest genererKontonummerRequest
    ) throws TransformerException {
        return testdataService.genererKontonummerOgSendTilTps(koeNavn, genererKontonummerRequest);
    }
}
