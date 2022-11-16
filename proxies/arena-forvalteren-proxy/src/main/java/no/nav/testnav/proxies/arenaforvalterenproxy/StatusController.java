package no.nav.testnav.proxies.arenaforvalterenproxy;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class StatusController {
    private static final String TEAM = "Team Arena";

    @GetMapping(value = "/internal/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Map<String, String>> getStatus() {
        var statusWebClient = WebClient.builder().build();

        var status = checkConsumerStatus(
                "https://arena-forvalteren.dev.adeo.no/internal/isAlive",
                "https://arena-forvalteren.dev.adeo.no/internal/isReady",
                statusWebClient);
        status.put("team", TEAM);

        return Map.of(
                "arena-forvalteren", status
        );
    }

    public Map<String, String> checkConsumerStatus(String aliveUrl, String readyUrl, WebClient webClient) {
        ConcurrentHashMap<String, String> status = new ConcurrentHashMap<>();

        Thread blockingThread = new Thread(() -> {
            status.put("alive", checkStatus(webClient, aliveUrl).block());
            status.put("ready", checkStatus(webClient, readyUrl).block());
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
