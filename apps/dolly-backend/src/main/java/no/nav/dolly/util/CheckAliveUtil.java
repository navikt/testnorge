package no.nav.dolly.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.status.v1.TestnavStatusResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;

import static java.util.Objects.nonNull;

@UtilityClass
@Slf4j
public final class CheckAliveUtil {

    final String TEAM_DOLLY = "Team Dolly";
    private static final String PATTERN = "%s, URL: %s";

    public static TestnavStatusResponse checkConsumerStatus(String aliveUrl, String readyUrl, WebClient webClient) {
        return TestnavStatusResponse.builder()
                .alive(checkInternal(webClient, aliveUrl))
                .ready(checkInternal(webClient, readyUrl))
                .team(TEAM_DOLLY)
                .build();
    }

    private String checkInternal(WebClient webClient, String url) {
        try {
            ResponseEntity<Void> response = webClient.get().uri(url)
                    .retrieve().toBodilessEntity()
                    .block();
            if (nonNull(response) && response.getStatusCode().is2xxSuccessful()) {
                return "OK";
            }
        } catch (WebClientResponseException ex) {
            String feilmelding = PATTERN.formatted(ex.getStatusCode(), url);
            log.error(feilmelding, ex);
            return feilmelding;
        } catch (Exception e) {
            log.error("Feilet under sjekk av status for {}", url, e);
            return e.getMessage();
        }
        return null;
    }
}
