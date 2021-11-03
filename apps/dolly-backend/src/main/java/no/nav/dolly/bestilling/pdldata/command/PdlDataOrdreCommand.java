package no.nav.dolly.bestilling.pdldata.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
public record PdlDataOrdreCommand(WebClient webClient, String ident, String token) implements Callable<Mono<String>> {

    private static final String PDL_FORVALTER_ORDRE_URL = "/api/v1/personer";
    private static final String IS_TPS_MASTER = "isTpsMaster";

    public Mono<String> call() {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PDL_FORVALTER_ORDRE_URL)
                        .pathSegment(ident)
                        .pathSegment("ordre")
                        .queryParam(IS_TPS_MASTER, true)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty());
    }
}
