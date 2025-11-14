package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;

@Slf4j
@RequiredArgsConstructor
public class AnnullerSamboerCommand implements Callable<Mono<PensjonforvalterResponse>> {
    private static final String PEN_SAMBOER_URL = "/pensjon/{miljoe}/api/samboer/periode/{periodeId}/annuller";

    private final WebClient webClient;
    private final String periodeId;
    private final String miljoe;
    private final String token;

    @Override
    public Mono<PensjonforvalterResponse> call() {
        var callId = generateCallId();
        log.info("Pensjon samboer annuller periodeId {}, callId: {}", periodeId, callId);
        return webClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(PEN_SAMBOER_URL)
                        .build(miljoe, periodeId))
                .headers(WebClientHeader.bearer(token))
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve()
                .toBodilessEntity()
                .map(response -> pensjonforvalterResponse(miljoe, periodeId, HttpStatus.valueOf(response.getStatusCode().value())))
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(error -> Mono.just(pensjonforvalterResponseFromError(miljoe, periodeId, error)));
    }

    private static PensjonforvalterResponse pensjonforvalterResponse(String miljoe, String persiodeId, HttpStatus status) {

        var miljoeResponse = PensjonforvalterResponse.ResponseEnvironment.builder()
                .miljo(miljoe)
                .response(PensjonforvalterResponse.Response.builder()
                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                .status(status.value())
                                .reasonPhrase(status.getReasonPhrase())
                                .build())
                        .path(PEN_SAMBOER_URL.replace("{miljoe}", miljoe).replace("{periodeId}", persiodeId))
                        .build())
                .build();

        return PensjonforvalterResponse.builder()
                .status(Collections.singletonList(miljoeResponse))
                .build();
    }

    private static PensjonforvalterResponse pensjonforvalterResponseFromError(String miljoe, String periodeId, Throwable throwable) {
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
                        .message(description.getMessage())
                        .path(PEN_SAMBOER_URL.replace("{miljoe}", miljoe).replace("{periodeId}", periodeId))
                        .build())
                .build();
        return PensjonforvalterResponse
                .builder()
                .status(Collections.singletonList(miljoeResponse))
                .build();
    }

}