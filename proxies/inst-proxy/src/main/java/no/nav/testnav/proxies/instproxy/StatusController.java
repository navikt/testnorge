package no.nav.testnav.proxies.instproxy;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.status.v1.TestnavStatusResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class StatusController {

    private static final String TEAM_ROCKET = "Team Rocket";

    private final WebClient webClient;

    @GetMapping(value = "/internal/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, TestnavStatusResponse> getStatus() {

        var serviceStatus = checkConsumerStatus(
                "https://institusjon-opphold-testdata.dev.intern.nav.no/internal/health/liveness",
                "https://institusjon-opphold-testdata.dev.intern.nav.no/internal/health/readiness",
                webClient);
        return Map.of(
                "inst-testdata", serviceStatus
        );

    }

    public TestnavStatusResponse checkConsumerStatus(String aliveUrl, String readyUrl, WebClient webClient) {
        TestnavStatusResponse status = TestnavStatusResponse.builder().team(TEAM_ROCKET).build();

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
                .map(result -> result.startsWith("Error:") ? result : "OK");
    }
}
