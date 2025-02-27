package no.nav.dolly.bestilling;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.dto.status.v1.TestnavStatusResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class ConsumerStatus {

    public abstract String serviceUrl();

    public abstract String consumerName();

    public String getLivenessEndpoint() {
        return "/internal/health/liveness";
    }

    public String getReadinessEndpoint() {
        return "/internal/health/readiness";
    }

    public Map<String, TestnavStatusResponse> checkStatus(WebClient webClient) {

        var consumerStatus = CheckAliveUtil.checkConsumerStatus(
                serviceUrl() + getLivenessEndpoint(),
                serviceUrl() + getReadinessEndpoint(),
                webClient);

        var statusMap = new ConcurrentHashMap<String, TestnavStatusResponse>();
        statusMap.put(consumerName(), consumerStatus);

        webClient
                .get()
                .uri(serviceUrl() + "/internal/status")
                .retrieve()
                .bodyToMono(new TypeReference())
                .timeout(Duration.ofSeconds(5))
                .doOnError(throwable -> log.error("Klarte ikke å hente status for {}", serviceUrl(), throwable))
                .onErrorReturn(new ConcurrentHashMap<>())
                .blockOptional()
                .orElse(new ConcurrentHashMap<>())
                .forEach((key, value) -> statusMap
                        .put(key, TestnavStatusResponse
                                .builder()
                                .team(value.get("team"))
                                .alive(value.get("alive"))
                                .ready(value.get("ready"))
                                .build()));

        return statusMap;
    }

    private static class TypeReference extends ParameterizedTypeReference<Map<String, Map<String, String>>> {
    }

}
