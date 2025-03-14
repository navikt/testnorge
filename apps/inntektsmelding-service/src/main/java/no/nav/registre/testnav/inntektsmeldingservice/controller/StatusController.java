package no.nav.registre.testnav.inntektsmeldingservice.controller;

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

    private static final String TEAM = "Team Dolly";

    private final WebClient webClient;

    @GetMapping(value = "/internal/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, TestnavStatusResponse> getStatus() {
        var status = checkConsumerStatus(
                "https://testnav-inntektsmelding-generator-service.intern.dev.nav.no/internal/health/liveness",
                "https://testnav-inntektsmelding-generator-service.intern.dev.nav.no/internal/health/readiness",
                webClient);
        return Map.of(
                "testnav-inntektsmelding-generator-service", status
        );
    }

    public TestnavStatusResponse checkConsumerStatus(String aliveUrl, String readyUrl, WebClient webClient) {
        var dollyStatus = TestnavStatusResponse.builder().team(TEAM).build();

        Thread blockingThread = new Thread(() -> {
            dollyStatus.setAlive(checkStatus(webClient, aliveUrl).block());
            dollyStatus.setReady(checkStatus(webClient, readyUrl).block());
        });
        blockingThread.start();
        try {
            blockingThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return dollyStatus;
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
