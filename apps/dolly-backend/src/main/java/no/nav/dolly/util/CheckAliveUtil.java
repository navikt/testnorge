package no.nav.dolly.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

@UtilityClass
@Slf4j
public final class CheckAliveUtil {

    private static final String PATTERN = "%s, URL: %s";

    public static Map<String, Object> checkConsumerStatus(String aliveUrl, String readyUrl, WebClient webClient) {
        var map = new HashMap<String, Object>();
        map.put("alive", checkIsAlive(webClient, aliveUrl));
        map.put("ready", checkIsAlive(webClient, readyUrl));
        return map;
    }

    private String checkIsAlive(WebClient webClient, String url) {
        try {
            ResponseEntity<Void> response = webClient.get().uri(url)
                    .retrieve().toBodilessEntity()
                    .block();
            if (nonNull(response) && response.getStatusCode().is2xxSuccessful()) {
                return response.getStatusCode().name();
            }
        } catch (WebClientResponseException ex) {
            String feilmelding = PATTERN.formatted(ex.getStatusCode(), url);
            log.error(feilmelding, ex);
            return feilmelding;
        } catch (Exception e) {
            return e.getMessage();
        }
        return null;
    }
}
