package no.nav.testnav.proxies.brregstubproxy;

import no.nav.testnav.libs.dto.status.v1.TestnavStatusResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class StatusController {
    private static final String TEAM = "Team Dolly";

    @GetMapping(value = "/internal/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, TestnavStatusResponse> getStatus() {
        var statusWebClient = WebClient.builder().build();

        var status = checkConsumerStatus(
                "https://brreg-stub.intern.dev.nav.no/isAlive",
                "https://brreg-stub.intern.dev.nav.no/isReady",
                statusWebClient);

        return Map.of(
                "brreg-stub", status
        );
    }

    public TestnavStatusResponse checkConsumerStatus(String aliveUrl, String readyUrl, WebClient webClient) {
        TestnavStatusResponse status = TestnavStatusResponse.builder().team(TEAM).build();

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
