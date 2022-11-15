package no.nav.testnav.proxies.sigrunstubproxy;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class StatusController {
    private static final String TEAM_INNTEKT = "Team Inntekt";

    @GetMapping(value = "/internal/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Map<String, String>> getStatus() {
        var statusWebClient = WebClient.builder().build();

        var pensjonStatus = checkConsumerStatus(
                "https://sigrun-skd-stub.dev.adeo.no/internal/isAlive",
                "https://sigrun-skd-stub.dev.adeo.no/internal/isAlive", // samme url brukes for begge
                statusWebClient);
        pensjonStatus.put("team", TEAM_INNTEKT);

        return Map.of(
                "sigrun-skd-stub", pensjonStatus
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
                .onErrorResume(Exception.class, error -> Mono.just("Error: " + error.getMessage()))
                .doOnSuccess(result -> Mono.just("OK"))
                .map(result -> result.startsWith("Error:") ? result : "OK");
    }
}
