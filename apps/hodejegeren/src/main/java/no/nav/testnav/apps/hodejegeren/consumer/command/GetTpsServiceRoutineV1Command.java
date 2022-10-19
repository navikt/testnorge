package no.nav.testnav.apps.hodejegeren.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetTpsServiceRoutineV1Command implements Callable<Mono<String>> {
    private final WebClient webClient;
    private final String token;
    private final String routineName;
    private final String aksjonsKode;
    private final String miljoe;
    private final String fnr;

    @Override
    public Mono<String> call() {
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/serviceroutine/{routineName}")
                        .queryParam("aksjonsKode", aksjonsKode)
                        .queryParam("environment", miljoe)
                        .queryParam("fnr", fnr)
                        .build(routineName)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(String.class);
    }
}
