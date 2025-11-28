package no.nav.dolly.bestilling.pdldata.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pdldata.dto.PdlResponse;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import static no.nav.dolly.util.RequestTimeout.REQUEST_DURATION;

@RequiredArgsConstructor
@Slf4j
public class PdlDataOppdateringCommand implements Callable<Mono<PdlResponse>> {

    private static final String PDL_FORVALTER_PERSONER_URL = "/api/v1/personer/{ident}";

    private final WebClient webClient;
    private final String ident;
    private final PersonUpdateRequestDTO body;
    private final String token;

    public Mono<PdlResponse> call() {
        return webClient
                .put()
                .uri(PDL_FORVALTER_PERSONER_URL, ident)
                .headers(WebClientHeader.bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(REQUEST_DURATION))
                .map(resultat -> PdlResponse.builder()
                        .ident(resultat)
                        .status(HttpStatus.OK)
                        .build())
                .onErrorMap(TimeoutException.class, e -> new HttpTimeoutException("Timeout on PUT of ident %s".formatted(ident)))
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> PdlResponse.of(WebClientError.describe(throwable)));
    }
}
