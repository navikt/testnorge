package no.nav.registre.testnorge.identservice.testdata;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.identservice.testdata.request.TpsHentFnrHistMultiServiceRoutineRequest;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.TpsRequestSender;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsRequestContext;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.User;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.response.TpsServiceRoutineResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.rmi.server.LogStream.log;
import static no.nav.registre.testnorge.identservice.testdata.utils.TpsRequestParameterCreator.opprettParametereForM201TpsRequest;

@Slf4j
@Service
public class FiltrerPaaIdenterTilgjengeligIMiljo {

    private static final User DOLLY_USER = new User("Dolly", "Dolly");

    @Autowired
    private TpsRequestSender tpsRequestSender;

    public Set<String> filtrerPaaIdenter(Set<String> identer) {

        Map<String, Object> tpsRequestParameters = opprettParametereForM201TpsRequest(identer, "A2");

        TpsRequestContext context = new TpsRequestContext();
        context.setUser(DOLLY_USER);

        Set<String> tilgjengeligeIdenterAlleMiljoer = newHashSet((Collection<String>) tpsRequestParameters.get("fnr"));

        context.setEnvironment("q2");

        TpsHentFnrHistMultiServiceRoutineRequest request = new TpsHentFnrHistMultiServiceRoutineRequest();
        request.setAntallFnr("1");
        request.setFnr(identer.toArray(new String[0]));
        request.setAksjonsKode("A");
        request.setAksjonsKode2("2");
        request.setServiceRutinenavn(tpsRequestParameters.get("serviceRutinenavn").toString());

        TpsServiceRoutineResponse tpsResponse = null;
        int loopCount = 3;
        do {
            tpsResponse = tpsRequestSender.sendTpsRequest(request, context);
            log.info(tpsResponse.getXml());
            loopCount--;
        } while (loopCount != 0 && tpsResponse.getXml().isEmpty());

        checkForTpsSystemfeil(tpsResponse);

        return tilgjengeligeIdenterAlleMiljoer;
    }

    private void checkForTpsSystemfeil(TpsServiceRoutineResponse response) {
        if (response.getXml().isEmpty()) {
            FiltrerPaaIdenterTilgjengeligIMiljo.log.error("Request mot TPS fikk timeout. Sjekk av tilgjengelighet på ident i miljoe feilet.");
        }
    }
}
