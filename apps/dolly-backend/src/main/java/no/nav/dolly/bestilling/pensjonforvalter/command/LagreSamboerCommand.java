package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonSamboerRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class LagreSamboerCommand implements Callable<Mono<PensjonforvalterResponse>> {
    private static final String PEN_SAMBOER_URL = "/{miljoe}/api/samboer";

    private final WebClient webClient;
    private final PensjonSamboerRequest pensjonSamboerRequest;
    private final String miljoe;
    private final String token;

    public Mono<PensjonforvalterResponse> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PEN_SAMBOER_URL)
                        .build(miljoe))
                .header(AUTHORIZATION, "Bearer " + token)
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(pensjonSamboerRequest)
                .retrieve()
                .toBodilessEntity()
                .map(response -> pensjonforvalterResponse(miljoe, HttpStatus.valueOf(response.getStatusCode().value())))
                .doOnError(WebClientFilter::logErrorMessage)
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

    private static PensjonforvalterResponse pensjonforvalterResponseFromError(String miljoe, Throwable error) {

        var miljoeResponse = PensjonforvalterResponse.ResponseEnvironment.builder()
                .miljo(miljoe)
                .response(PensjonforvalterResponse.Response.builder()
                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                .status(WebClientFilter.getStatus(error).value())
                                .reasonPhrase(WebClientFilter.getStatus(error).getReasonPhrase())
                                .build())
                        .message(WebClientFilter.getMessage(error))
                        .path(PEN_SAMBOER_URL.replace("{miljoe}", miljoe))
                        .build())
                .build();

        return PensjonforvalterResponse.builder()
                .status(Collections.singletonList(miljoeResponse))
                .build();
    }
}