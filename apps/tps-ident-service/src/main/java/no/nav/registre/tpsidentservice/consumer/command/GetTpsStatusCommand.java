package no.nav.registre.tpsidentservice.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import no.nav.registre.tpsidentservice.consumer.dto.TpsStatus;

@RequiredArgsConstructor
public class GetTpsStatusCommand implements Callable<TpsStatus> {
    private final WebClient webClient;
    private final String ident;

    @Override
    public TpsStatus call() {
        return webClient
                .get()
                .uri(builder -> builder
                        .path("api/v1/testdata/tpsStatus")
                        .queryParam("identer", ident)
                        .build())
                .retrieve()
                .bodyToMono(TpsStatus.class)
                .block();
    }
}
