package no.nav.dolly.bestilling;

import no.nav.dolly.util.CheckAliveUtil;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface ConsumerStatus {

    String serviceUrl();

    String consumerName();

    default Map<String, Object> checkStatus(WebClient webClient) {
        final String TEAM_DOLLY = "Team Dolly";

        var consumerStatus = CheckAliveUtil.checkConsumerStatus(
                serviceUrl() + "/internal/isAlive",
                serviceUrl() + "/internal/isReady",
                webClient);

        consumerStatus.put("team", TEAM_DOLLY);

        var statusMap = new ConcurrentHashMap<String, Object>();
        statusMap.put(consumerName(), consumerStatus);

        try {
            var response = webClient.get()
                    .uri(serviceUrl() + "/internal/status")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            statusMap.putAll(response);
        } catch (Exception e) {
            // Ignored.
        }

        return statusMap;
    }
}
