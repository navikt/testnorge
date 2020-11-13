package no.nav.registre.testnorge.identservice.testdata.servicerutiner.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.GetTpsServiceRutinerService;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.definition.TpsServiceRoutineDefinitionRequest;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.message.MessageProvider;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests.TpsServiceRoutineRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class RsTpsRequestMappingUtils {

    private final ObjectMapper objectMapper;
    private final MessageProvider messageProvider;
    private final GetTpsServiceRutinerService getTpsServiceRutinerService;

    public <T> T convert(Map<String, Object> params, Class<T> type) {
        return objectMapper.convertValue(params, type);
    }

    public TpsServiceRoutineRequest convertToTpsServiceRoutineRequest(String serviceRutineNavn, Map<String, Object> map) {
        return convertToTpsServiceRoutineRequest(serviceRutineNavn, map, false);
    }

    public TpsServiceRoutineRequest convertToTpsServiceRoutineRequest(String serviceRutineNavn, Map<String, Object> map, boolean validateRequestParameters) {
        TpsServiceRoutineDefinitionRequest tpsServiceRoutineDefinitionRequest = getTpsServiceRutinerService.execute()
                .stream()
                .filter(request -> request.getName().equalsIgnoreCase(serviceRutineNavn))
                .findFirst().orElse(null);

        Class<?> requestClass = nonNull(tpsServiceRoutineDefinitionRequest) ? tpsServiceRoutineDefinitionRequest
                .getJavaClass() : null;

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
