package no.nav.dolly.bestilling;

import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.dto.status.v1.TestnavStatusResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface ConsumerStatus {

    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ConsumerStatus.class);

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
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Map<String, String>>>() {
                    })
                    .block();
            response.forEach((key, value) -> statusMap.put(key, TestnavStatusResponse.builder()
                    .team(value.get("team"))
                    .alive(value.get("alive"))
                    .ready(value.get("ready"))
                    .build()));
        } catch (Exception e) {
            log.error("Klarte ikke å hente status for {}", serviceUrl(), e);
            // Ignored.
        }

        return statusMap;
    }
}
