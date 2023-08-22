package no.nav.dolly.bestilling;

import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.dto.status.v1.TestnavStatusResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface ConsumerStatus {

    String serviceUrl();

    String consumerName();

    default Map<String, TestnavStatusResponse> checkStatus(WebClient webClient) {

        var consumerStatus = CheckAliveUtil.checkConsumerStatus(
                serviceUrl() + "/internal/isAlive",
                serviceUrl() + "/internal/isReady",
                webClient);

        var statusMap = new ConcurrentHashMap<String, TestnavStatusResponse>();
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
