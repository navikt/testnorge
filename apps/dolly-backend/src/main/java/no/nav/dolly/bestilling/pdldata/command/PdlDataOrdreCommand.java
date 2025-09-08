package no.nav.dolly.bestilling.pdldata.command;

import io.netty.handler.timeout.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pdldata.dto.PdlResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClientRequest;

import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.RequestTimeout.REQUEST_DURATION;

@RequiredArgsConstructor
@Slf4j
public class PdlDataOrdreCommand implements Callable<Mono<PdlResponse>> {

    private static final String PDL_FORVALTER_ORDRE_URL = "/api/v1/personer/{ident}/ordre";
    private static final String EXCLUDE_EKSTERNE_PERSONER = "ekskluderEksternePersoner";

    private final WebClient webClient;
    private final String ident;
    private final boolean ekskluderEksternePersoner;
    private final String token;

    public Mono<PdlResponse> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(PDL_FORVALTER_ORDRE_URL)
                        .queryParam(EXCLUDE_EKSTERNE_PERSONER, ekskluderEksternePersoner)
                        .build(ident))
                .httpRequest(httpRequest -> {
                    HttpClientRequest reactorRequest = httpRequest.getNativeRequest();
                    reactorRequest.responseTimeout(Duration.ofSeconds(REQUEST_DURATION));
                })
                .headers(WebClientHeader.bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .map(reultat -> PdlResponse.builder()
                        .jsonNode(reultat)
                        .status(HttpStatus.OK)
                        .build())
                .onErrorMap(TimeoutException.class, e -> new HttpTimeoutException("Timeout on POST of ident %s".formatted(ident)))
                .doOnError(WebClientError.logTo(log))

                .onErrorResume(throwable -> PdlResponse.of(WebClientError.describe(throwable)));
    }

}
