package no.nav.dolly.bestilling.pdldata.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
public record PdlDataSlettCommand(WebClient webClient,
                                  String ident, String token) implements Callable<Mono<Void>> {

    private static final String PDL_FORVALTER_URL = "/api/v1/personer/{ident}";

    public Mono<Void> call() {

        return webClient
                .delete()
                .uri(PDL_FORVALTER_URL, ident)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty());
    }
}
