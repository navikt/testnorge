package no.nav.testnav.proxies.pdlproxy;

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

    private static final String TEAM_PDL = "Team Persondata";

    private final WebClient webClient;

    @GetMapping(value = "/internal/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, TestnavStatusResponse> getStatus() {

        var pdlTestdataStatus = checkConsumerStatus(
                "https://pdl-testdata.dev.intern.nav.no/internal/health/liveness",
                "https://pdl-testdata.dev.intern.nav.no/internal/health/readiness",
                webClient);

        var pdlApiStatus = checkConsumerStatus(
                "https://pdl-api.dev.intern.nav.no/internal/health/liveness",
                "https://pdl-api.dev.intern.nav.no/internal/health/readiness",
                webClient);

        var pdlApiQ1Status = checkConsumerStatus(
                "https://pdl-api-q1.dev.intern.nav.no/internal/health/liveness",
                "https://pdl-api-q1.dev.intern.nav.no/internal/health/readiness",
                webClient);

        var pdlIdenthendelseStatus = checkConsumerStatus(
                "https://pdl-identhendelse-lager.dev.intern.nav.no/internal/health/liveness",
                "https://pdl-identhendelse-lager.dev.intern.nav.no/internal/health/readiness",
                webClient);

        var pdlAktorStatus = checkConsumerStatus(
                "https://pdl-aktor.dev.intern.nav.no/internal/health/liveness",
                "https://pdl-aktor.dev.intern.nav.no/internal/health/readiness",
                webClient);

        return Map.of(
                "pdl-testdata", pdlTestdataStatus,
                "pdl-api", pdlApiStatus,
                "pdl-api-q1", pdlApiQ1Status,
                "pdl-identhendelse", pdlIdenthendelseStatus,
                "pdl-aktor", pdlAktorStatus
        );
    }

    public TestnavStatusResponse checkConsumerStatus(String aliveUrl, String readyUrl, WebClient webClient) {
        TestnavStatusResponse status = TestnavStatusResponse.builder().team(TEAM_PDL).build();

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