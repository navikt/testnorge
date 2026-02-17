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
    public Mono<Map<String, TestnavStatusResponse>> getStatus() {
        return checkConsumerStatus(
                "https://testnav-inntektsmelding-generator-service.intern.dev.nav.no/internal/health/liveness",
                "https://testnav-inntektsmelding-generator-service.intern.dev.nav.no/internal/health/readiness")
                .map(status -> Map.of("testnav-inntektsmelding-generator-service", status));
    }

    private Mono<TestnavStatusResponse> checkConsumerStatus(String aliveUrl, String readyUrl) {
        return Mono.zip(
                checkStatus(aliveUrl),
                checkStatus(readyUrl)
        ).map(tuple -> TestnavStatusResponse.builder()
                .team(TEAM)
                .alive(tuple.getT1())
                .ready(tuple.getT2())
                .build());
    }

    private Mono<String> checkStatus(String url) {
        return webClient.get().uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .defaultIfEmpty("OK")
                .onErrorResume(Exception.class, error -> Mono.just("Error: " + error.getMessage()))
                .map(result -> result.startsWith("Error:") ? result : "OK");
    }
}
