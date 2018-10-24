package no.nav.registre.hodejegeren.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import no.nav.registre.hodejegeren.consumer.TpsfConsumer;

@Service
@Getter
public class TpsStatusQuoService {

    @Autowired
    private TpsfConsumer tpsfConsumer;

    private Map<String, JsonNode> tpsServiceRoutineCache;

    public Map<String, String> getStatusQuo(List<String> feltnavn, String aksjonsKode, String environment, String fnr) throws IOException {
        Map<String, String> personStatusQuo = new HashMap<>(feltnavn.size());
        tpsServiceRoutineCache = new HashMap<>();

        for (String felt : feltnavn) {
            JsonNode root = feltnavnMapper(felt, aksjonsKode, environment, fnr);

            if (root == null) {
                // error handling
            }

            personStatusQuo.put(felt, extractStatusQuoInfoFromTps(root, felt));
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
        if (tpsServiceRoutineCache == null) tpsServiceRoutineCache = new HashMap<>();

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

    public JsonNode feltnavnMapper(String felt, String aksjonsKode, String environment, String fnr) throws IOException {
        String routineName = null;

        switch (felt) {
        case "boAdresse1":
        case "boAdresse2":
        case "boPoststed":
        case "bolignr":
        case "datoDo":
        case "datoFlyttet":
        case "datoInnvandret":
        case "datoSivilstand":
        case "datoStatsborger":
        case "datoUmyndiggjort":
        case "datoUtvandret":
        case "etternavn":
        case "fnr":
        case "fornavn":
        case "innvandretFra":
        case "kommunenr":
        case "kortnavn":
        case "mellomnavn":
        case "personStatus":
        case "postAdresse1":
        case "postAdresse2":
        case "postAdresse3":
        case "postnr":
        case "sivilstand":
        case "spesregType":
        case "statsborger":
        case "tidligereKommunenr":
        case "tknr":
        case "utvandretTil":
            routineName = "FS03-FDNUMMER-PERSDATA-O";
            break;
        case "boPostnr":
        case "doSaksbehandler":
        case "doSystem":
        case "doTidspunkt":
        case "fodested":
        case "fodestedSaksbehandler":
        case "fodestedSystem":
        case "fodestedTidspunkt":
        case "fodselsdato":
        case "fodselsnummer":
        case "fnrSaksbehandler":
        case "fnrSystem":
        case "fnrTidspunkt":
        case "identType":
        case "kjonn":
        case "navnSaksbehandler":
        case "navnSystem":
        case "navnTidspunkt":
        case "gjeldendePersonnavn":
        case "datoPersonstatus":
        case "kodePersonstatus":
        case "kodePersonstatusBeskr":
        case "psSaksbehandler":
        case "psSystem":
        case "psTidspunkt":
        case "postLand":
        case "postLandKode":
        case "postPostnr":
        case "postPoststed":
        case "kodeSivilstand":
        case "kodeSivilstandBeskr":
        case "sivilstSaksbehandler":
        case "sivilstSystem":
        case "sivilstTidspunkt":
        case "statsborgerskap":
        case "datoStatsborgerskap":
        case "kodeStatsborgerskap":
        case "kodeStatsborgerskapBeskr":
        case "sbSaksbehandler":
        case "sbSystem":
        case "sbTidspunkt":
            routineName = "FS03-FDNUMMER-KERNINFO-O";
            break;
        case "adresse":
        case "antallRelasjoner":
        case "endringsDato":
        case "relasjon/adresseStatus":
        case "relasjon/datoDo":
        case "relasjon/etternavn:":
        case "relasjon/fnrRelasjon":
        case "relasjon/fornavn":
        case "relasjon/kortnavn":
        case "relasjon/mellomnavn":
        case "relasjon/spesregType":
        case "relasjon/typeRelasjon":
            routineName = "FS03-FDNUMMER-PERSRELA-O";
            break;
        default:
            break;
        }

        return getInfoOnRoutineName(routineName, aksjonsKode, environment, fnr);
    }
}
