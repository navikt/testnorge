package no.nav.dolly.bestilling.pdldata.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.net.http.HttpTimeoutException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
@Slf4j
public class PdlDataStanaloneCommand implements Callable<Mono<String>> {

    private static final String PDL_FORVALTER_IDENTER_STANDALONE_URL = "/api/v1/identiteter/{ident}/standalone/{standalone}";

    private final WebClient webClient;
    private final String ident;
    private final Boolean standalone;
    private final String token;

    public Mono<String> call() {
        return webClient
                .put()
                .uri(uriBuilder -> uriBuilder.path(PDL_FORVALTER_IDENTER_STANDALONE_URL)
                        .build(ident, standalone))
                .headers(WebClientHeader.bearer(token))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .toBodilessEntity()
                .map(response -> "OK")
                .onErrorMap(TimeoutException.class, e -> new HttpTimeoutException("Timeout on PUT for ident %s".formatted(ident)))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Mono.just(ident))
                .doOnError(WebClientError.logTo(log));
    }
}