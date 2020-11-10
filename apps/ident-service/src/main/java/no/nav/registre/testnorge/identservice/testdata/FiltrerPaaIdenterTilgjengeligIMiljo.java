package no.nav.registre.testnorge.identservice.testdata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.identservice.testdata.request.TpsHentFnrHistMultiServiceRoutineRequest;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.TpsRequestSender;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsRequestContext;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.User;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.response.TpsServiceRoutineResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.nonNull;
import static no.nav.registre.testnorge.identservice.testdata.utils.TpsRequestParameterCreator.opprettParametereForM201TpsRequest;

@Slf4j
@Service
public class FiltrerPaaIdenterTilgjengeligIMiljo {

    private static final User DOLLY_USER = new User("Dolly", "Dolly");

    @Autowired
    private TpsRequestSender tpsRequestSender;

    public String filtrerPaaIdenter(String ident) throws IOException {

        Map<String, Object> tpsRequestParameters = opprettParametereForM201TpsRequest(ident, "A2");

        TpsRequestContext context = new TpsRequestContext();
        context.setUser(DOLLY_USER);
        context.setEnvironment("q2");

        TpsHentFnrHistMultiServiceRoutineRequest request = new TpsHentFnrHistMultiServiceRoutineRequest();
        request.setAntallFnr("1");
        request.setFnr(ident);
        request.setAksjonsKode("A");
        request.setAksjonsKode2("2");
        request.setServiceRutinenavn(tpsRequestParameters.get("serviceRutinenavn").toString());

        TpsServiceRoutineResponse tpsResponse = null;
        int loopCount = 3;
        do {
            tpsResponse = tpsRequestSender.sendTpsRequest(request, context);
            loopCount--;
        } while (loopCount != 0 && tpsResponse.getXml().isEmpty());

        return getDescriptiveMessage(tpsResponse);
    }

    private String getDescriptiveMessage(TpsServiceRoutineResponse response) throws IOException {
        if (response.getXml().isEmpty()) {
            log.error("Request mot TPS fikk timeout. Sjekk av tilgjengelighet på ident i miljoe feilet.");
            return "TPS Timeout";
        }

        XmlMapper xmlMapper = new XmlMapper();
        JsonNode node = xmlMapper.readTree(response.getXml().getBytes());
        JsonNode tpsSvar = nonNull(node.get("tpsSvar")) ? node.get("tpsSvar") : node;
        JsonNode tpsSvarStatus = nonNull(tpsSvar.get("svarStatus")) ? tpsSvar.get("svarStatus") : node;

        return nonNull(tpsSvarStatus.get("utfyllendeMelding")) ? tpsSvarStatus.get("utfyllendeMelding").asText() : "Felt ikke funnet";

    }
}
