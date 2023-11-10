package no.nav.testnav.apps.hodejegeren.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetTpsStatusPaaIdenterCommand implements Callable<Mono<String>> {

    private final WebClient webClient;
    private final String token;
    private final String aksjonsKode;
    private final int antallIdenter;
    private final String miljoe;
    private final String identer;

    @Override
    public Mono<String> call() {
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/serviceroutine/FS03-FDLISTER-DISKNAVN-M")
                        .queryParam("aksjonsKode", aksjonsKode)
                        .queryParam("antallFnr", antallIdenter)
                        .queryParam("environment", miljoe)
                        .queryParam("nFnr", identer)
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(String.class);
    }
}
