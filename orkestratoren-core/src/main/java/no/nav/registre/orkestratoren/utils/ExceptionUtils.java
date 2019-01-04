package no.nav.registre.orkestratoren.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.web.client.HttpStatusCodeException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionUtils {

    public static Collection<? extends Long> extractIdsFromResponseBody(HttpStatusCodeException e) {
        List<Long> ids = new ArrayList<>();
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(e.getResponseBodyAsString()).get("ids");
            if (jsonNode == null) {
                log.warn("Finner ikke id-er i response body til exception fra Hodejegeren - Body: {}", e.getResponseBodyAsString());
            } else {
                for (final JsonNode idNode : jsonNode) {
                    ids.add(idNode.asLong());
                }
            }
        } catch (IOException ie) {
            log.warn("Kunne ikke deserialisere innholdet i exception fra Hodejegeren");
        }
        return ids;
    }

    /**
     * Metoden tar inn en liste av id-er og oppretter en ny liste der inkrementerende id-er er skrevet som range for å spare plass
     * (f.eks vil 1, 2, 3 skrives som 1 - 3)
     */
    public static List<String> createListOfRangesFromIds(List<Long> ids) {
        List<String> idsWithRange = new ArrayList<>();
        boolean rangeStarted = false;
        StringBuilder rangeToAdd = new StringBuilder();

        for (int i = 0; i < ids.size(); i++) {
            if (i >= ids.size() - 1) {
                if (rangeStarted) {
                    rangeToAdd.append(ids.get(i));
                    idsWithRange.add(rangeToAdd.toString());
                } else {
                    idsWithRange.add(ids.get(i).toString());
                }
                break;
            }
            if (ids.get(i + 1) == ids.get(i) + 1) {
                if (!rangeStarted) {
                    rangeToAdd = new StringBuilder(ids.get(i).toString()).append(" - ");
                    rangeStarted = true;
                }
            } else {
                if (rangeStarted) {
                    rangeToAdd.append(ids.get(i));
                    idsWithRange.add(rangeToAdd.toString());
                    rangeStarted = false;
                } else {
                    idsWithRange.add(ids.get(i).toString());
                }
            }
        }
        return idsWithRange;
    }
}
