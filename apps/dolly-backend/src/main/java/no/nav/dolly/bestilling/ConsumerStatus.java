package no.nav.dolly.bestilling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.service.CheckAliveService;
import no.nav.testnav.libs.dto.status.v1.TestnavStatusResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public abstract class ConsumerStatus {

    public static final String LIVENESS_ENDPOINT = "/internal/health/liveness";
    public static final String READINESS_ENDPOINT = "/internal/health/readiness";

    private final CheckAliveService checkAliveService;

    public abstract String serviceUrl();

    public abstract String consumerName();

    public Mono<Map<String, TestnavStatusResponse>> checkStatus(WebClient webClient) {

        return checkAliveService.checkConsumerStatus(
                        serviceUrl() + LIVENESS_ENDPOINT,
                        serviceUrl() + READINESS_ENDPOINT,
                        webClient)
                .map(consumerResponce -> Map.of(consumerName(), consumerResponce))
                .flatMap(consumerStatus ->
                        webClient
                                .get()
                                .uri(serviceUrl() + "/internal/status")
                                .retrieve()
                                .bodyToMono(new TypeReference())
                                .timeout(Duration.ofSeconds(5))
                                .flatMap(resultat ->
                                        Flux.fromIterable(resultat.entrySet())
                                                .collectMap(Map.Entry::getKey, entry -> TestnavStatusResponse
                                                        .builder()
                                                        .team(entry.getValue().get("team"))
                                                        .alive(entry.getValue().get("alive"))
                                                        .ready(entry.getValue().get("ready"))
                                                        .build()))
                                .zipWith(Mono.just(consumerStatus))
                                .onErrorResume(ignore -> Mono.just(Map.of(consumerName(), new TestnavStatusResponse()))
                                        .zipWith(Mono.just(consumerStatus)))
                                .map(tuple -> List.of(tuple.getT1(), tuple.getT2())))
                .flatMapMany(Flux::fromIterable)
                .reduce(new HashMap<>(),
                        (map, status) -> {
                            map.putAll(status);
                            return map;
                        });
    }

    private static class TypeReference extends ParameterizedTypeReference<Map<String, Map<String, String>>> {
    }
}