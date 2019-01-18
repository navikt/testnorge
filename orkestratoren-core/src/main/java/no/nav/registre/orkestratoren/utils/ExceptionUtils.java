package no.nav.registre.orkestratoren.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.web.client.HttpStatusCodeException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import no.nav.registre.orkestratoren.exceptions.HttpStatusCodeExceptionContainer;

@Slf4j
public class ExceptionUtils {

    public static Collection<? extends Long> extractIdsFromResponseBody(HttpStatusCodeException e) {
        List<Long> ids = new ArrayList<>();
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(e.getResponseBodyAsString()).get("ids");
            if (jsonNode == null) {
                log.warn("Finner ikke id-er i response body til exception fra Testnorge-Skd - Body: {}", e.getResponseBodyAsString());
            } else {
                for (final JsonNode idNode : jsonNode) {
                    ids.add(idNode.asLong());
                }
            }
        } catch (IOException ie) {
            log.warn("Kunne ikke deserialisere innholdet i exception fra Testnorge-Skd: {} - {}", ie.getMessage(), ie);
            log.warn("HttpStatusCodeException fra Testnorge-Skd: {} - {} - {}", e.getStatusCode(), e.getMessage(), e);
        }
        return ids;
    }

    public static void filterStackTraceOnNavSpecificItems(HttpStatusCodeExceptionContainer exceptionContainer) {
        log.info("Stack trace in logs has been shortened. Disable method 'ExceptionUtils.filterStackTraceOnNavSpecificItems' in order to get full trace");
        StackTraceElement[] outerStackTraceElements = exceptionContainer.getStackTrace();
        if (outerStackTraceElements != null && outerStackTraceElements.length != 0) {
            List<StackTraceElement> stackTraceElements = new ArrayList<>(Arrays.asList(outerStackTraceElements));
            stackTraceElements.removeIf(stackTraceElement -> !stackTraceElement.getClassName().contains("no.nav"));
            exceptionContainer.setStackTrace(stackTraceElements.toArray(new StackTraceElement[0])); // NOSONAR - Sonar tror det er raskere å definere str. på array her
        }
        Iterator<HttpStatusCodeException> httpStatusCodeExceptionIterator = exceptionContainer.getNestedExceptions().iterator();
        while (httpStatusCodeExceptionIterator.hasNext()) {
            HttpStatusCodeException exception = httpStatusCodeExceptionIterator.next();
            List<StackTraceElement> stackTraceElements = new ArrayList<>(Arrays.asList(exception.getStackTrace()));
            stackTraceElements.removeIf(stackTraceElement -> !stackTraceElement.getClassName().contains("no.nav"));
            exception.setStackTrace(stackTraceElements.toArray(new StackTraceElement[0])); // NOSONAR - Sonar tror det er raskere å definere str. på array her
        }
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
