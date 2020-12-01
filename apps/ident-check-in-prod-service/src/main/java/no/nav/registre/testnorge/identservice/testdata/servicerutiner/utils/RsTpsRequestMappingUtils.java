package no.nav.registre.testnorge.identservice.testdata.servicerutiner.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.definition.TpsServiceRoutineDefinitionRequest;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.message.MessageProvider;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsServiceRoutineRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RsTpsRequestMappingUtils {

    private final ObjectMapper objectMapper;
    private final MessageProvider messageProvider;

    public <T> T convert(Map<String, Object> params, Class<T> type) {
        return objectMapper.convertValue(params, type);
    }

    public TpsServiceRoutineRequest convertToTpsServiceRoutineRequest(Map<String, Object> map, boolean validateRequestParameters) {
        TpsServiceRoutineDefinitionRequest tpsServiceRoutineDefinitionRequest = new TpsServiceRoutineDefinitionRequest();

        Class<?> requestClass = tpsServiceRoutineDefinitionRequest.getJavaClass();

        if (validateRequestParameters) {
            validateTpsRequestParameters(tpsServiceRoutineDefinitionRequest, map);
        }

        return (TpsServiceRoutineRequest) objectMapper.convertValue(map, requestClass);
    }

    public void validateTpsRequestParameters(TpsServiceRoutineDefinitionRequest tpsServiceRoutineDefinitionRequest, Map<String, Object> map) {
        List<String> requiredParameterNameList = tpsServiceRoutineDefinitionRequest.getRequiredParameterNameList();
        requiredParameterNameList.removeAll(map.keySet());
        if (!requiredParameterNameList.isEmpty()) {
            throw new RuntimeException(messageProvider.get("rest.service.request.exception.required", requiredParameterNameList));
        }
    }
}
