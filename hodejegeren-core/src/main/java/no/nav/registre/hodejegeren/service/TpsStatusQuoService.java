package no.nav.registre.hodejegeren.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.hodejegeren.consumer.TpsfConsumer;

@Service
@Getter
@Slf4j
public class TpsStatusQuoService {

    @Autowired
    private TpsfConsumer tpsfConsumer;

    private Map<String, JsonNode> tpsServiceRoutineCache;

    public Map<String, String> getStatusQuo(String routineName, List<String> feltnavn, String aksjonsKode, String environment, String fnr) throws IOException {
        Map<String, String> personStatusQuo = new HashMap<>(feltnavn.size());
        resetCache();

        for (String felt : feltnavn) {
            JsonNode root = getInfoOnRoutineName(routineName, aksjonsKode, environment, fnr);

            if (root == null) {
                log.info("Could not get routine " + routineName + " on fnr " + fnr);
                throw new NullPointerException();
            } else {
                personStatusQuo.put(felt, extractStatusQuoInfoFromTps(root, felt));
            }
        }

        return personStatusQuo;
    }

    public String extractStatusQuoInfoFromTps(JsonNode root, String felt) {
        if (felt.contains("/")) {
            String[] feltene = felt.split("/");

            int i;
            for (i = 0; i < feltene.length - 1; i++) {
                root = root.findValue(feltene[i]);
            }

            return root.findValue(feltene[i]).asText();
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