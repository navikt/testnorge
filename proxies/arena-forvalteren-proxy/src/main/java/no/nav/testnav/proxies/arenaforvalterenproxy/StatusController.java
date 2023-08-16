package no.nav.testnav.proxies.arenaforvalterenproxy;

import no.nav.testnav.libs.dto.status.v1.DollyStatusResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class StatusController {
    private static final String TEAM = "Team Arena";

    @GetMapping(value = "/internal/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, DollyStatusResponse> getStatus() {
        var statusWebClient = WebClient.builder().build();

        var status = checkConsumerStatus(
                "https://arena-forvalteren.dev.adeo.no/internal/isAlive",
                "https://arena-forvalteren.dev.adeo.no/internal/isReady",
                statusWebClient);

        return Map.of(
                "arena-forvalteren", status
        );
    }

    public DollyStatusResponse checkConsumerStatus(String aliveUrl, String readyUrl, WebClient webClient) {
        DollyStatusResponse status = DollyStatusResponse.builder().team(TEAM).build();

        Thread blockingThread = new Thread(() -> {
            status.setAlive(checkStatus(webClient, aliveUrl).block());
            status.setReady(checkStatus(webClient, readyUrl).block());
        });
        blockingThread.start();
        try {
            blockingThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return status;
    }

    private Mono<String> checkStatus(WebClient webClient, String url) {
        return webClient.get().uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .defaultIfEmpty("OK")
                .onErrorResume(Exception.class, error -> Mono.just("Error: " + error.getMessage()))
                .doOnSuccess(result -> Mono.just("OK"))
                .map(result -> result.startsWith("Error:") ? result : "OK");
    }
}