package no.nav.registre.testnorge.identservice.testdata.servicerutiner.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import no.nav.registre.testnorge.identservice.testdata.response.Response;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.response.TpsServiceRoutineResponse;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RsTpsResponseMappingUtils {
    public static final String STATUS_KEY = "status";
    public TpsServiceRoutineResponse convertToTpsServiceRutineResponse(Response response) throws IOException {
        TpsServiceRoutineResponse tpsServiceRutineResponse = new TpsServiceRoutineResponse();

        tpsServiceRutineResponse.setXml(response.getRawXml());

        JSONObject jsonObject = new JSONObject();
        if (response.getDataXmls() != null) {
            List<JSONObject> data = response.getDataXmls()
                    .stream()
                    .map(XML::toJSONObject)
                    .collect(Collectors.toList());

            int antallHit = 0;
            for(int i = 0; i < data.size(); i++){
                jsonObject.put("data"+(i+1), data.get(i));
                antallHit++;
            }
            response.setTotalHits(antallHit);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        Map data = objectMapper.readValue(jsonObject.toString(), Map.class);
        data.put(STATUS_KEY, response.getStatus());
        data.put("antallTotalt", response.getTotalHits());
        tpsServiceRutineResponse.setResponse(data);

        return tpsServiceRutineResponse;
    }
}

