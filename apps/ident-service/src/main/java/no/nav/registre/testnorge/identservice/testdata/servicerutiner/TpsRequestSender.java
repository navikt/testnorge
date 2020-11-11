package no.nav.registre.testnorge.identservice.testdata.servicerutiner;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.identservice.service.TpsRequestService;
import no.nav.registre.testnorge.identservice.testdata.response.Response;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.definition.TpsServiceRoutineDefinitionRequest;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsRequestContext;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsServiceRoutineRequest;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.response.TpsServiceRoutineResponse;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.utils.RsTpsResponseMappingUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class TpsRequestSender {
    private static final long DEFAULT_LES_TIMEOUT = 5000;

    private final FindServiceRoutineByName findServiceRoutineByName;
    private final RsTpsResponseMappingUtils rsTpsResponseMappingUtils;
    private final TpsRequestService tpsRequestService;

    @SneakyThrows
    public TpsServiceRoutineResponse sendTpsRequest(TpsServiceRoutineRequest request, TpsRequestContext context, long timeout) {
        Optional<TpsServiceRoutineDefinitionRequest> serviceRoutine = findServiceRoutineByName.execute();
        if (serviceRoutine.isPresent()) {

            Response response = tpsRequestService.executeServiceRutineRequest(request, serviceRoutine.get(), context, timeout);
            log.info(response.toString());
            return rsTpsResponseMappingUtils.convertToTpsServiceRutineResponse(response);
        }
        return null;
    }

    public TpsServiceRoutineResponse sendTpsRequest(TpsServiceRoutineRequest request, TpsRequestContext context) {
        return sendTpsRequest(request, context, DEFAULT_LES_TIMEOUT);
    }
}
