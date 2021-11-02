package no.nav.dolly.bestilling.pdldata.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class PdlDataOrdreCommand implements Callable<Mono<String>> {

    private static final String PDL_FORVALTER_ORDRE_URL = "/api/v1/personer/{ident}/ordre";
    private static final String IS_TPS_MASTER = "isTpsMaster";

    private final WebClient webClient;
    private final String ident;
    private final boolean isTpsfMaster;
    private final String token;

    public Mono<String> call() {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(PDL_FORVALTER_ORDRE_URL)
                        .queryParam(IS_TPS_MASTER, isTpsfMaster)
                        .build(ident))
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty());
    }
}
