package no.nav.registre.testnorge.identservice.testdata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.identservice.testdata.request.TpsHentFnrHistMultiServiceRoutineRequest;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.TpsRequestSender;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsRequestContext;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.User;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.response.TpsServiceRoutineResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

import static java.util.Objects.nonNull;
import static no.nav.registre.testnorge.identservice.testdata.utils.TpsRequestParameterCreator.opprettParametereForM201TpsRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class FiltrerPaaIdenterTilgjengeligIMiljo {

    private static final User DOLLY_USER = new User("Dolly", "Dolly");

    private final TpsRequestSender tpsRequestSender;

    public ResponseEntity<String> filtrerPaaIdenter(String ident) throws IOException {

        Map<String, Object> tpsRequestParameters = opprettParametereForM201TpsRequest(ident, "A2");

        TpsRequestContext context = new TpsRequestContext();
        context.setUser(DOLLY_USER);
        context.setEnvironment("q2");

        TpsHentFnrHistMultiServiceRoutineRequest request = new TpsHentFnrHistMultiServiceRoutineRequest();
        request.setAntallFnr("1");
        request.setFnr(new String[]{ident});
        request.setAksjonsKode("A");
        request.setAksjonsKode2("2");
        request.setServiceRutinenavn(tpsRequestParameters.get("serviceRutinenavn").toString());

        TpsServiceRoutineResponse tpsResponse = tpsRequestSender.sendTpsRequest(request, context);

        return getDescriptiveMessage(ident, tpsResponse);
    }

    private ResponseEntity<String> getDescriptiveMessage(String ident, TpsServiceRoutineResponse response) throws IOException {

        if (response.getXml().isEmpty()) {
            log.error("Request mot TPS fikk timeout. Sjekk av tilgjengelighet på ident i miljoe feilet.");
            throw new RuntimeException("TPS Timeout");
        }

        XmlMapper xmlMapper = new XmlMapper();
        JsonNode node = xmlMapper.readTree(response.getXml().getBytes());
        JsonNode tpsSvar = nonNull(node.get("tpsSvar")) ? node.get("tpsSvar") : node;
        JsonNode tpsSvarStatus = nonNull(tpsSvar.get("svarStatus")) ? tpsSvar.get("svarStatus") : node;
        JsonNode tpsStatusKode = nonNull(tpsSvarStatus.get("returStatus")) ? tpsSvar.get("returStatus") : node;
        log.info(tpsStatusKode.asText());
        log.info(node.asText());

        return tpsStatusKode.asText().contains("00") ? ResponseEntity.ok(ident) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
