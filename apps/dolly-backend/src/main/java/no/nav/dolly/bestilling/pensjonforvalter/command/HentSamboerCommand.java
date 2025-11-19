package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonSamboerResponse;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.CallIdUtil.generateCallId;

@Slf4j
@RequiredArgsConstructor
public class HentSamboerCommand implements Callable<Mono<PensjonSamboerResponse>> {
    private static final String PEN_SAMBOER_URL = "/pensjon/{miljoe}/api/samboer/{ident}";

    private final WebClient webClient;
    private final String ident;
    private final String miljoe;
    private final String token;

    public Mono<PensjonSamboerResponse> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(PEN_SAMBOER_URL)
                        .queryParam("historikk", true)
                        .build(miljoe, ident))
                .headers(WebClientHeader.bearer(token))
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve()
                .bodyToMono(PensjonSamboerResponse.class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(error -> Mono.just(pensjonforvalterResponseFromError(miljoe, error)));
    }

    private static PensjonSamboerResponse pensjonforvalterResponseFromError(String miljoe, Throwable throwable) {
        var description = WebClientError.describe(throwable);
        var miljoeResponse = PensjonforvalterResponse.ResponseEnvironment
                .builder()
                .miljo(miljoe)
                .response(PensjonforvalterResponse.Response
                        .builder()
                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                .status(description.getStatus().value())
                                .reasonPhrase(description.getStatus().getReasonPhrase())
                                .build())
                        .message(description.getMessage().replaceAll("\"timestamp\":\\d+,", ""))
                        .path(PEN_SAMBOER_URL.replace("{miljoe}", miljoe))
                        .build())
                .build();
        return PensjonSamboerResponse
                .builder()
                .status(Collections.singletonList(miljoeResponse))
                .build();
    }
}