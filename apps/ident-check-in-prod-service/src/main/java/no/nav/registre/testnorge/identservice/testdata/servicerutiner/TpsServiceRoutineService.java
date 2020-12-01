package no.nav.registre.testnorge.identservice.testdata.servicerutiner;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsRequestContext;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsServiceRoutineRequest;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.response.TpsServiceRoutineResponse;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.utils.RsTpsRequestMappingUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TpsServiceRoutineService {

    private static final String TPS_SERVICE_ROUTINE_PARAM_NAME = "FS03-FDLISTER-DISKNAVN-M";
    private static final String ENVIRONMENT_PARAM_NAME = "q1";

    private final RsTpsRequestMappingUtils mappingUtils;
    private final TpsRequestSender tpsRequestSender;

    public TpsServiceRoutineResponse execute(String serviceRoutinenavn, Map<String, Object> tpsRequestParameters, boolean validateRequestParameters) {
        tpsRequestParameters.put(TPS_SERVICE_ROUTINE_PARAM_NAME, serviceRoutinenavn);

        TpsRequestContext context = new TpsRequestContext();
        context.setEnvironment(tpsRequestParameters.get(ENVIRONMENT_PARAM_NAME).toString());

        TpsServiceRoutineRequest tpsServiceRoutineRequest = mappingUtils.convertToTpsServiceRoutineRequest(tpsRequestParameters, validateRequestParameters);

        return tpsRequestSender.sendTpsRequest(tpsServiceRoutineRequest, context);
    }
}
