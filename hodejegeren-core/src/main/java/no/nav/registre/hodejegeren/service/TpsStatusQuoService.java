package no.nav.registre.hodejegeren.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import no.nav.registre.hodejegeren.consumer.TpsfConsumer;
import no.nav.registre.hodejegeren.exception.ManglendeInfoITpsException;

@Service
@Getter
@Slf4j
public class TpsStatusQuoService {

    public static final String AKSJONSKODE = "A0";
    @Autowired
    private TpsfConsumer tpsfConsumer;
    private Map<String, JsonNode> tpsServiceRoutineCache;

    public Map<String, String> hentStatusQuo(String routineName, List<String> feltnavn, String environment, String fnr) throws IOException {
        Map<String, String> personStatusQuo = new HashMap<>(feltnavn.size());
        resetCache();

        for (String felt : feltnavn) {
            JsonNode root = getInfoOnRoutineName(routineName, AKSJONSKODE, environment, fnr);

            if (root == null) {
                log.error("Fant ikke rutine {} på fnr {}", routineName, fnr);

                throw new ManglendeInfoITpsException("Fant ikke rutine " + routineName + " på fnr " + fnr);
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
            if (jsonArray.isEmpty()) {
                return "";
            }
            return jsonArray.get(0).toString();
        } else {
            String verdiAvFelt;
            try {
                verdiAvFelt = root.findValue(felt).asText();
            } catch (NullPointerException e) {
                log.error("Kunne ikke finne status quo fra TPS på felt {} i json-node {}", felt, root);
                throw e;
            }
            return verdiAvFelt;
        }
    }

    public JsonNode getInfoOnRoutineName(String routineName, String aksjonsKode, String environment, String fnr) throws IOException {
        if (this.tpsServiceRoutineCache == null) {
            resetCache();
        }

        if (!tpsServiceRoutineCache.containsKey(routineName)) {
            tpsServiceRoutineCache.put(routineName, tpsfConsumer.getTpsServiceRoutine(routineName, aksjonsKode, environment, fnr));
        }
        return tpsServiceRoutineCache.get(routineName);
    }

    private void resetCache() {
        this.tpsServiceRoutineCache = new HashMap<>();
    }
}