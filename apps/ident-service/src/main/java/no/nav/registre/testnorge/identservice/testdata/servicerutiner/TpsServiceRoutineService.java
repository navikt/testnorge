package no.nav.registre.testnorge.identservice.testdata.servicerutiner;

import no.nav.registre.testnorge.identservice.testdata.servicerutiner.authorization.UserContextHolder;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsRequestContext;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsServiceRoutineRequest;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.response.TpsServiceRoutineResponse;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.utils.RsTpsRequestMappingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TpsServiceRoutineService {

    private static final String TPS_SERVICE_ROUTINE_PARAM_NAME = "serviceRutinenavn";
    private static final String ENVIRONMENT_PARAM_NAME = "environment";


    @Autowired
    private UserContextHolder userContextHolder;

    @Autowired
    private RsTpsRequestMappingUtils mappingUtils;

    @Autowired
    private TpsRequestSender tpsRequestSender;

    public TpsServiceRoutineResponse execute(String serviceRoutinenavn, Map<String, Object> tpsRequestParameters, boolean validateRequestParameters){
        tpsRequestParameters.put(TPS_SERVICE_ROUTINE_PARAM_NAME, serviceRoutinenavn);

        TpsRequestContext context = new TpsRequestContext();
        context.setUser(userContextHolder.getUser());
        context.setEnvironment(tpsRequestParameters.get(ENVIRONMENT_PARAM_NAME).toString());

        TpsServiceRoutineRequest tpsServiceRoutineRequest = mappingUtils.convertToTpsServiceRoutineRequest(serviceRoutinenavn, tpsRequestParameters, validateRequestParameters);

        return tpsRequestSender.sendTpsRequest(tpsServiceRoutineRequest, context);
    }
}
