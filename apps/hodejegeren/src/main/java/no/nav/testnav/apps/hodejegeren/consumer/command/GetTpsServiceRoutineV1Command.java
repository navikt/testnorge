package no.nav.testnav.apps.hodejegeren.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetTpsServiceRoutineV1Command implements Callable<String> {
    private final WebClient webClient;
    private final String routineName;
    private final String aksjonsKode;
    private final String miljoe;
    private final String fnr;

    @Override
    public String call() {
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/serviceroutine/{routineName}")
                        .queryParam("aksjonsKode", aksjonsKode)
                        .queryParam("environment", miljoe)
                        .queryParam("fnr", fnr)
                        .build(routineName)
                )
                .retrieve()
                .bodyToMono(String.class)
                .block();

    }
}
