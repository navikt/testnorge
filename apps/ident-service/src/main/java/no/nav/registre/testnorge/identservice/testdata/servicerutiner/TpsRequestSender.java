package no.nav.registre.testnorge.identservice.testdata.servicerutiner;

import lombok.SneakyThrows;
import no.nav.registre.testnorge.identservice.service.TpsRequestService;
import no.nav.registre.testnorge.identservice.testdata.response.Response;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.definition.TpsServiceRoutineDefinitionRequest;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsRequestContext;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsServiceRoutineRequest;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.response.TpsServiceRoutineResponse;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.utils.RsTpsResponseMappingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class TpsRequestSender {
    public static final long DEFAULT_LES_TIMEOUT = 5000;

    @Autowired
    private FindServiceRoutineByName findServiceRoutineByName;

    @Autowired
    private RsTpsResponseMappingUtils rsTpsResponseMappingUtils;

    @Autowired
    private TpsRequestService tpsRequestService;

    @SneakyThrows
    public TpsServiceRoutineResponse sendTpsRequest(TpsServiceRoutineRequest request, TpsRequestContext context, long timeout) {
        Optional<TpsServiceRoutineDefinitionRequest> serviceRoutine = findServiceRoutineByName.execute(request.getServiceRutinenavn());
        if (serviceRoutine.isPresent()) {

            Response response = tpsRequestService.executeServiceRutineRequest(request, serviceRoutine.get(), context, timeout);
            return rsTpsResponseMappingUtils.convertToTpsServiceRutineResponse(response);
        }
        return null;
    }

    public TpsServiceRoutineResponse sendTpsRequest(TpsServiceRoutineRequest request, TpsRequestContext context) {
        return sendTpsRequest(request, context, DEFAULT_LES_TIMEOUT);
    }
}
