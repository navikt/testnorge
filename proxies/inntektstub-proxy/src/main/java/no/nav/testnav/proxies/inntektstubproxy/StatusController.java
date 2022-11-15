package no.nav.testnav.proxies.inntektstubproxy;

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
    final String TEAM = "Team Inntekt";

    @GetMapping(value = "/internal/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Map<String, String>> getStatus() {
        var statusWebClient = WebClient.builder().build();

        var pensjonStatus = checkConsumerStatus(
                "https://inntektstub.dev.adeo.no/internal/isAlive",
                "https://inntektstub.dev.adeo.no/internal/isReady",
                statusWebClient);
        pensjonStatus.put("team", TEAM);

        return Map.of(
                "inntektstub", pensjonStatus
        );
    }

    public Map<String, String> checkConsumerStatus(String aliveUrl, String readyUrl, WebClient webClient) {
        ConcurrentHashMap<String, String> status = new ConcurrentHashMap();

        Thread blockingThread = new Thread(() -> {
            var serviceStatus = Stream.of(
                    checkStatus(webClient, new CheckRequest("alive", aliveUrl)),
                    checkStatus(webClient, new CheckRequest("ready", readyUrl))
            )
            .map(request -> request.block())
            .collect(Collectors.toMap(CheckResponse::getType, CheckResponse::getResponse));

            status.putAll(serviceStatus);
        });
        blockingThread.start();
        try {
            blockingThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return status;
    }

    static class CheckRequest {
        public String type;
        public String url;

        public CheckRequest(String type, String url) {
            this.type = type;
            this.url = url;
        }

        public String getType() {
            return type;
        }

        public String getUrl() {
            return url;
        }
    }

    static class CheckResponse {
        public String type;
        public String response;

        public CheckResponse(String type, String response) {
            this.type = type;
            this.response = response;
        }

        public String getType() {
            return type;
        }

        public String getResponse() {
            return response;
        }
    }

    private Mono<CheckResponse> checkStatus(WebClient webClient, CheckRequest checkRequest) {
        return webClient.get().uri(checkRequest.url)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(Exception.class, error -> Mono.just("Error: " + error.getMessage()))
                .doOnSuccess(result -> Mono.just("OK"))
                .map(result -> new CheckResponse(checkRequest.type, result.startsWith("Error:") ? result : "OK"));
    }
}
