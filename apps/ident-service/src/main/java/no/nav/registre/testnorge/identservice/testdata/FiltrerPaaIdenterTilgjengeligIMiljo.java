package no.nav.registre.testnorge.identservice.testdata;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.TpsRequestSender;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsRequestContext;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsServiceRoutineRequest;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.User;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.response.TpsServiceRoutineResponse;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.utils.RsTpsRequestMappingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static no.nav.registre.testnorge.identservice.testdata.utils.TpsRequestParameterCreator.opprettParametereForM201TpsRequest;

@Slf4j
@Service
public class FiltrerPaaIdenterTilgjengeligIMiljo {

    public static final int MAX_ANTALL_IDENTER_PER_REQUEST = 80; // Service routine M201 maximum
    private static final String TPS_SYSTEM_ERROR_CODE = "12";
    private final User DOLLY_USER = new User("Dolly", "Dolly");

    @Autowired
    private TpsRequestSender tpsRequestSender;

    @Autowired
    private RsTpsRequestMappingUtils mappingUtils;

    public Set<String> filtrerPaaIdenter(Collection<String> identer) {

        Map<String, Object> tpsRequestParameters = opprettParametereForM201TpsRequest(identer, "A2");

        TpsRequestContext context = new TpsRequestContext();
        context.setUser(DOLLY_USER);

        Set<String> tilgjengeligeIdenterAlleMiljoer = newHashSet((Collection<String>) tpsRequestParameters.get("fnr"));

            context.setEnvironment("q2");

            TpsServiceRoutineRequest tpsServiceRoutineRequest = mappingUtils.convertToTpsServiceRoutineRequest(String.valueOf(tpsRequestParameters
                    .get("serviceRutinenavn")), tpsRequestParameters);

            TpsServiceRoutineResponse tpsResponse = null;
            int loopCount = 3;
            do {
                tpsResponse = tpsRequestSender.sendTpsRequest(tpsServiceRoutineRequest, context);
                loopCount--;
            } while (loopCount != 0 && tpsResponse.getXml().isEmpty());

            checkForTpsSystemfeil(tpsResponse);

        return tilgjengeligeIdenterAlleMiljoer;
    }

    private void checkForTpsSystemfeil(TpsServiceRoutineResponse response) {
        if (response.getXml().isEmpty()) {
            log.error("Request mot TPS fikk timeout. Sjekk av tilgjengelighet på ident i miljoe feilet.");
        }
    }
}
