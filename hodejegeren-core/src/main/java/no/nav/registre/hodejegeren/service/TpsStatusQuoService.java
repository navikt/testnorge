package no.nav.registre.hodejegeren.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import no.nav.registre.hodejegeren.consumer.TpsfConsumer;
import no.nav.registre.hodejegeren.exception.ManglendeInfoITpsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Getter
@Slf4j
public class TpsStatusQuoService {

    @Autowired
    private TpsfConsumer tpsfConsumer;

    private Map<String, JsonNode> tpsServiceRoutineCache;

    public static final String AKSJONSKODE = "A0";

    public Map<String, String> getStatusQuo(String routineName, List<String> feltnavn, String environment, String fnr) throws IOException {
        Map<String, String> personStatusQuo = new HashMap<>(feltnavn.size());
        resetCache();

        for (String felt : feltnavn) {
            JsonNode root = getInfoOnRoutineName(routineName, AKSJONSKODE, environment, fnr);

            if (root == null) {
                if (log.isInfoEnabled()) {
                    log.info("Could not get routine {} on fnr {}", routineName, fnr);
                }

                throw new ManglendeInfoITpsException("Could not get routine " + routineName + " on fnr " + fnr);
            } else {
                personStatusQuo.put(felt, extractStatusQuoInfoFromTps(root, felt));
            }
        }

        return personStatusQuo;
    }

    public String extractStatusQuoInfoFromTps(JsonNode root, String felt) {
        if (felt.contains("$")) {
            Object document = Configuration.defaultConfiguration().jsonProvider().parse(root.toString());
            JSONArray jsonArray = JsonPath.read(document, felt);
            return jsonArray.get(0).toString();
        } else {
            return root.findValue(felt).asText();
        }
    }

    public JsonNode getInfoOnRoutineName(String routineName, String aksjonsKode, String environment, String fnr) throws IOException {
        if (this.tpsServiceRoutineCache == null) {
            resetCache();
        }

        if (!tpsServiceRoutineCache.containsKey(routineName)) {
            tpsServiceRoutineCache.put(routineName, getInfoHelper(routineName, aksjonsKode, environment, fnr));
        }
        return tpsServiceRoutineCache.get(routineName);
    }

    public JsonNode getInfoHelper(String routineName, String aksjonsKode, String environment, String fnr) throws IOException {
        Map<String, Object> tpsRequestParameters = new HashMap<>();
        tpsRequestParameters.put("aksjonsKode", aksjonsKode);
        tpsRequestParameters.put("environment", environment);
        tpsRequestParameters.put("fnr", fnr);

        return tpsfConsumer.getTpsServiceRoutine(routineName, tpsRequestParameters);
    }

    private void resetCache() {
        this.tpsServiceRoutineCache = new HashMap<>();
    }
}