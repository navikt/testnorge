package no.nav.testnav.proxies.skjermingsregisterproxy;

import no.nav.testnav.libs.dto.status.v1.TestnavStatusResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class StatusController {
    private static final String TEAM = "Team Org (NOM)";

    @GetMapping(value = "/internal/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, TestnavStatusResponse> getStatus() {
        var statusWebClient = WebClient.builder().build();

        var status = checkConsumerStatus(
                "http://skjermede-personer.nom.svc.nais.local" + "/internal/isAlive",
                "http://skjermede-personer.nom.svc.nais.local" + "/internal/isReady",
                statusWebClient);

        return Map.of(
                "skjermede-personer", status
        );
    }

    public TestnavStatusResponse checkConsumerStatus(String aliveUrl, String readyUrl, WebClient webClient) {
        TestnavStatusResponse status = TestnavStatusResponse.builder().team(TEAM).build();

        Thread blockingThread = new Thread(() -> {
            status.setAlive(
                    checkStatus(webClient, aliveUrl)
                            .blockOptional()
                            .orElse("Error: Empty response")
            );
            status.setReady(
                    checkStatus(webClient, readyUrl)
                            .blockOptional()
                            .orElse("Error: Empty response")
            );
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
