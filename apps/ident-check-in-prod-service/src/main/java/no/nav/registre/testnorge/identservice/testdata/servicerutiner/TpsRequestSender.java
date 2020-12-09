package no.nav.registre.testnorge.identservice.testdata.servicerutiner;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.nav.registre.testnorge.identservice.service.TpsRequestService;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsRequestContext;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsServiceRoutineRequest;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.response.TpsServiceRoutineResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TpsRequestSender {
    private static final long DEFAULT_LES_TIMEOUT = 5000;

    private final TpsRequestService tpsRequestService;

    @SneakyThrows
    public TpsServiceRoutineResponse sendTpsRequest(TpsServiceRoutineRequest request, TpsRequestContext context, long timeout) {

        return tpsRequestService.executeServiceRutineRequest(request, context, timeout);
    }

    public TpsServiceRoutineResponse sendTpsRequest(TpsServiceRoutineRequest request, TpsRequestContext context) {
        return sendTpsRequest(request, context, DEFAULT_LES_TIMEOUT);
    }
}
