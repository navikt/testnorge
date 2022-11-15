package no.nav.testnav.proxies.pdlproxy;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class StatusController {
    final String TEAM_PDL = "Team Persondata";

    @GetMapping(value = "/internal/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Map<String, String>> getStatus() {
        var statusWebClient = WebClient.builder().build();

        var pdlTestdataStatus = checkConsumerStatus(
                "https://pdl-testdata.dev.intern.nav.no/internal/health/liveness",
                "https://pdl-testdata.dev.intern.nav.no/internal/health/readiness",
                WebClient.builder().build());
        pdlTestdataStatus.put("team", TEAM_PDL);

        var pdlApiStatus = checkConsumerStatus(
                "https://pdl-api.dev.intern.nav.no/internal/health/liveness",
                "https://pdl-api.dev.intern.nav.no/internal/health/readiness",
                WebClient.builder().build());
        pdlApiStatus.put("team", TEAM_PDL);

        var pdlApiQ1Status = checkConsumerStatus(
                "https://pdl-api-q1.dev.intern.nav.no/internal/health/liveness",
                "https://pdl-api-q1.dev.intern.nav.no/internal/health/readiness",
                WebClient.builder().build());
        pdlApiQ1Status.put("team", TEAM_PDL);

        var pdlIdenthendelseStatus = checkConsumerStatus(
                "https://pdl-identhendelse-lager.dev.intern.nav.no/internal/health/liveness",
                "https://pdl-identhendelse-lager.dev.intern.nav.no/internal/health/readiness",
                WebClient.builder().build());
        pdlIdenthendelseStatus.put("team", TEAM_PDL);

        var pdlAktorStatus = checkConsumerStatus(
                "https://pdl-aktor.dev.intern.nav.no/internal/health/liveness",
                "https://pdl-aktor.dev.intern.nav.no/internal/health/readiness",
                WebClient.builder().build());
        pdlAktorStatus.put("team", TEAM_PDL);

        return Map.of(
                "pdl-testdata", pdlTestdataStatus,
                "pdl-api", pdlApiStatus,
                "pdl-api-q1", pdlApiQ1Status,
                "pdl-identhendelse", pdlIdenthendelseStatus,
                "pdl-aktor", pdlAktorStatus
        );
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
            throw new RuntimeException(e);
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
