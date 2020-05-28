package no.nav.registre.frikort.provider.rs;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;

import no.nav.registre.frikort.service.MqService;
import no.nav.registre.frikort.service.SyntetiseringService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static no.nav.registre.frikort.utils.SwaggerUtils.LEGG_PAA_KOE_DESCRIPTION;
import static no.nav.registre.frikort.utils.SwaggerUtils.REQUEST_BODY_DESCRIPTION;


@Slf4j
@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class SyntetiseringController {

    private final SyntetiseringService syntetiseringService;
    private final MqService mqService;

    @PostMapping(value = "/generer")
    @ApiOperation(value = "Generer syntetiske egenandelsmeldinger som XML string.")
    public List<String> genererEgenandelsmeldinger(
            @ApiParam(value = REQUEST_BODY_DESCRIPTION, required = true)
            @RequestBody() Map<String, Integer> request,
            @ApiParam(value = LEGG_PAA_KOE_DESCRIPTION)
            @RequestParam(defaultValue = "true") boolean leggPaaKoe) throws JAXBException {

        List<String> xmlMeldinger = syntetiseringService.hentSyntetiskeEgenandelerSomXML(request);
        log.info("{} egenandelsmelding(er) ble generert og gjort om til XMLString.", xmlMeldinger.size());

        if (leggPaaKoe) {
            for (String melding : xmlMeldinger) {
                mqService.leggTilMeldingPaaKoe(melding);
            }
            log.info("Generert(e) egenandelsmelding(er) ble lagt til på kø.");
        }
        return xmlMeldinger;
    }

}
