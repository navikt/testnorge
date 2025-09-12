package no.nav.dolly.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.status.v1.TestnavStatusResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@UtilityClass
public class CheckAliveUtil {

    private static final String TEAM_DOLLY = "Team Dolly";
    private static final String PATTERN = "%s, URL: %s";

    public static Mono<TestnavStatusResponse> checkConsumerStatus(String aliveUrl, String readyUrl, WebClient webClient) {

        return Mono.just(TEAM_DOLLY)
                .flatMap(ignore -> Mono.zip(
                        checkInternal(webClient, aliveUrl),
                        checkInternal(webClient, readyUrl)))
                .map(tuple -> TestnavStatusResponse.builder()
                        .alive(tuple.getT1())
                        .ready(tuple.getT2())
                        .team(TEAM_DOLLY)
                        .build());
    }

    private static Mono<String> checkInternal(WebClient webClient, String url) {

        return webClient.get()
                .uri(url)
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful() ? "OK" :
                        "Error: " + response.getStatusCode())
                .onErrorResume(throwable -> Mono.just("Error: " + throwable.getMessage()))
                .doOnError(error -> log.error(PATTERN.formatted("Error during health check", url), error));
    }
}
