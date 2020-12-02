package no.nav.registre.testnorge.identservice.testdata.servicerutiner.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.identservice.testdata.request.TpsHentFnrHistMultiServiceRoutineRequest;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsServiceRoutineRequest;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class RsTpsRequestMappingUtils {

    private final ObjectMapper objectMapper;

    public <T> T convert(Map<String, Object> params, Class<T> type) {
        return objectMapper.convertValue(params, type);
    }

    public TpsServiceRoutineRequest convertToTpsServiceRoutineRequest(Map<String, Object> map) {

        return objectMapper.convertValue(map, TpsHentFnrHistMultiServiceRoutineRequest.class);
    }
}
