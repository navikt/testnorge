package no.nav.registre.hodejegeren.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.hodejegeren.consumer.TpsfConsumer;
import no.nav.registre.hodejegeren.exception.ManglendeInfoITpsException;

@Service
@Getter
@Slf4j
public class TpsStatusQuoService {

    public static final String AKSJONSKODE = "B0";

    @Autowired
    private TpsfConsumer tpsfConsumer;

    private Map<String, JsonNode> tpsServiceRoutineCache;

    public Map<String, String> hentStatusQuo(String routineName, List<String> feltnavn, String environment, String fnr) throws IOException {
        Map<String, String> personStatusQuo = new HashMap<>(feltnavn.size());
        resetCache();

        for (String felt : feltnavn) {
            JsonNode root = getInfoOnRoutineName(routineName, AKSJONSKODE, environment, fnr);

            if (root == null) {
                log.error("Fant ikke rutine {} på fnr {}", routineName.replaceAll("[\r\n]",""), fnr.replaceAll("[\r\n]",""));

                throw new ManglendeInfoITpsException("Fant ikke rutine " + routineName + " på fnr " + fnr);
            } else {
                personStatusQuo.put(felt, extractStatusQuoInfoFromTps(root, felt, fnr));
            }
        }

        return personStatusQuo;
    }

    public String extractStatusQuoInfoFromTps(JsonNode root, String felt, String fnr) {
        if (felt.contains("$")) {
            Object document = Configuration.defaultConfiguration().jsonProvider().parse(root.toString());
            JSONArray jsonArray = JsonPath.read(document, felt);
            if (jsonArray.isEmpty()) {
                return "";
            }
            return jsonArray.get(0).toString();
        } else {
            JsonNode statusQuoFromTPS = root.findValue(felt);
            if (statusQuoFromTPS == null) {
                JsonNode utfyllendeMelding = root.findValue("utfyllendeMelding");
                if (utfyllendeMelding == null) {
                    throw new ManglendeInfoITpsException(
                            "Kunne ikke finne status quo på person med fnr " + fnr + " for felt '" + felt);
                } else {
                    throw new ManglendeInfoITpsException(
                            "Kunne ikke finne status quo på person med fnr " + fnr + " for felt '" + felt + "'. Utfyllende melding fra TPS: " + utfyllendeMelding.asText());
                }
            }
            return statusQuoFromTPS.asText();
        }
    }

    public JsonNode getInfoOnRoutineName(String routineName, String aksjonsKode, String environment, String fnr) throws IOException {
        if (this.tpsServiceRoutineCache == null) {
            resetCache();
        }

        if (!tpsServiceRoutineCache.containsKey(routineName)) {
            JsonNode response = tpsfConsumer.getTpsServiceRoutine(routineName, aksjonsKode, environment, fnr);
            tpsServiceRoutineCache.put(routineName, response);
        }
        return tpsServiceRoutineCache.get(routineName);
    }

    public void resetCache() {
        this.tpsServiceRoutineCache = new HashMap<>();
    }
}