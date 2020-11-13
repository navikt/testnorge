package no.nav.registre.testnorge.identservice.testdata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.identservice.testdata.request.TpsHentFnrHistMultiServiceRoutineRequest;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.TpsRequestSender;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsRequestContext;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.User;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.response.TpsServiceRoutineResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpConnectTimeoutException;
import java.util.List;

import static java.util.Objects.isNull;
import static no.nav.registre.testnorge.identservice.testdata.ExtractDataFromTpsServiceRoutineResponse.trekkUtIdenterMedStatusFunnetFraResponse;
import static no.nav.registre.testnorge.identservice.testdata.consumers.config.MessageQueueConsumerConstants.SEARCH_ENVIRONMENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class FiltrerPaaIdenterTilgjengeligIMiljo {

    @Value("${mq.tps.serviceroutine}")
    private String tpsServicerutine;

    private static final User DOLLY_USER = new User("Dolly", "Dolly");

    private final TpsRequestSender tpsRequestSender;

    public ResponseEntity<List<String>> filtrerPaaIdenter(List<String> identer) throws IOException {

        TpsRequestContext context = new TpsRequestContext();
        context.setUser(DOLLY_USER);
        context.setEnvironment(SEARCH_ENVIRONMENT);

        String[] identerArray = new String[identer.size()];
        identerArray = identer.toArray(identerArray);

        TpsHentFnrHistMultiServiceRoutineRequest request = new TpsHentFnrHistMultiServiceRoutineRequest();
        request.setAntallFnr(String.valueOf(identer.size()));
        request.setFnr(identerArray);
        request.setAksjonsKode("A");
        request.setAksjonsKode2("2");
        request.setServiceRutinenavn(tpsServicerutine);

        TpsServiceRoutineResponse tpsResponse = tpsRequestSender.sendTpsRequest(request, context);

        return filtrerFunnedeIdenter(identer, tpsResponse);
    }

    private ResponseEntity<List<String>> filtrerFunnedeIdenter(List<String> identer, TpsServiceRoutineResponse response) throws IOException {

        if (isNull(response) || response.getXml().isEmpty()) {
            log.error("Request mot TPS fikk timeout. Sjekk av tilgjengelighet på ident i miljoe feilet.");
            throw new HttpConnectTimeoutException("TPS Timeout");
        }

        identer.removeAll(trekkUtIdenterMedStatusFunnetFraResponse(response));

        return !identer.isEmpty() ? ResponseEntity.ok(identer) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
