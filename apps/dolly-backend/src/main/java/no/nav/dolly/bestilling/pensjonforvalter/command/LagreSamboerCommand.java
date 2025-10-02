package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonSamboerRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.CallIdUtil.generateCallId;

@Slf4j
@RequiredArgsConstructor
public class LagreSamboerCommand implements Callable<Mono<PensjonforvalterResponse>> {
    private static final String PEN_SAMBOER_URL = "/{miljoe}/api/samboer";

    private final WebClient webClient;
    private final PensjonSamboerRequest pensjonSamboerRequest;
    private final String miljoe;
    private final String token;

    public Mono<PensjonforvalterResponse> call() {

        var callId = generateCallId();
        log.info("Pensjon samboer opprett i {} {}, callId: {}", miljoe, pensjonSamboerRequest, callId);

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PEN_SAMBOER_URL)
                        .build(miljoe))
                .headers(WebClientHeader.bearer(token))
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(pensjonSamboerRequest)
                .retrieve()
                .toBodilessEntity()
                .map(response -> pensjonforvalterResponse(miljoe, HttpStatus.valueOf(response.getStatusCode().value())))
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(error -> Mono.just(pensjonforvalterResponseFromError(miljoe, error)));
    }

    private static PensjonforvalterResponse pensjonforvalterResponse(String miljoe, HttpStatus status) {

        var miljoeResponse = PensjonforvalterResponse.ResponseEnvironment.builder()
                .miljo(miljoe)
                .response(PensjonforvalterResponse.Response.builder()
                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                .status(status.value())
                                .reasonPhrase(status.getReasonPhrase())
                                .build())
                        .path(PEN_SAMBOER_URL.replace("{miljoe}", miljoe))
                        .build())
                .build();

        return PensjonforvalterResponse.builder()
                .status(Collections.singletonList(miljoeResponse))
                .build();
    }

    private static PensjonforvalterResponse pensjonforvalterResponseFromError(String miljoe, Throwable throwable) {
        var description = WebClientError.describe(throwable);
        var miljoeResponse = PensjonforvalterResponse.ResponseEnvironment
                .builder()
                .miljo(miljoe)
                .response(PensjonforvalterResponse.Response
                        .builder()
                        .httpStatus(PensjonforvalterResponse.HttpStatus
                                .builder()
                                .status(description.getStatus().value())
                                .reasonPhrase(description.getStatus().getReasonPhrase())
                                .build())
                        .message(description.getMessage()
                                .replaceAll("\"timestamp\":\\d+,", ""))
                        .path(PEN_SAMBOER_URL.replace("{miljoe}", miljoe))
                        .build())
                .build();
        return PensjonforvalterResponse
                .builder()
                .status(Collections.singletonList(miljoeResponse))
                .build();
    }

}