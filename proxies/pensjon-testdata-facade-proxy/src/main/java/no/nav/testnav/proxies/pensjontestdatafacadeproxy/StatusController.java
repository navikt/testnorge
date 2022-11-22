package no.nav.testnav.proxies.pensjontestdatafacadeproxy;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class StatusController {
    private static final String TEAM_PENSJON_TESTDATA = "Team Pentek (pensjontestdata)";

    @GetMapping(value = "/internal/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Map<String, String>> getStatus() {
        var statusWebClient = WebClient.builder().build();

        var pensjonStatus = checkConsumerStatus(
                "https://pensjon-testdata-facade.dev.intern.nav.no/isAlive",
                "https://pensjon-testdata-facade.dev.intern.nav.no/isReady",
                statusWebClient);
        pensjonStatus.put("team", TEAM_PENSJON_TESTDATA);

        var additionalStatus = additionalConsumerStatus("https://pensjon-testdata-facade.dev.intern.nav.no/api/v1/status", statusWebClient);

        var statusMap = new ConcurrentHashMap<String, Map<String, String>>();
        statusMap.put("pensjon-testdata", pensjonStatus);
        statusMap.putAll(additionalStatus);

        return statusMap;
    }

    public Map<String, Map<String, String>> additionalConsumerStatus(String url, WebClient webClient) {
        ConcurrentHashMap<String, Map<String, String>> status = new ConcurrentHashMap<>();

        Thread blockingThread = new Thread(() -> {
            try {
                Map response = webClient.get()
                        .uri(url)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();
                status.putAll(response);
            } catch (Exception e) {
            }
        });
        blockingThread.start();
        try {
            blockingThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return status;
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
                    .map(result -> result.startsWith("Error:") ? result : "OK");
    }
}
