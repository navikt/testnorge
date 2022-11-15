package no.nav.testnav.proxies.dokarkivproxy;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class StatusController {
    final String TEAM_DOKARKIV = "Team Dokumentløsninger";

    @GetMapping(value = "/internal/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Map<String, String>> getStatus() {
        var statusWebClient = WebClient.builder().build();

        return Stream.of("q1", "q2", "q4", "q5", "qx", "t0", "t1", "t2", "t3", "t4", "t5", "t13")
                .parallel()
                .map(miljo -> {
                    var miljoStatus = checkConsumerStatus(
                            "https://dokarkiv-" + miljo + ".dev.adeo.no/actuator/health/liveness",
                            "https://dokarkiv-" + miljo + ".dev.adeo.no/actuator/health/readiness",
                            statusWebClient);
                    miljoStatus.put("team", TEAM_DOKARKIV);
                    return Map.of(
                            "dokarkiv-" + miljo, miljoStatus
                    );
                })
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, String> checkConsumerStatus(String aliveUrl, String readyUrl, WebClient webClient) {
        ConcurrentHashMap<String, String> status = new ConcurrentHashMap();

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
