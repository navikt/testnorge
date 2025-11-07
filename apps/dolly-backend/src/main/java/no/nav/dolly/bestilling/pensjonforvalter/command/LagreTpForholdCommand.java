package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpForholdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.RequestTimeout.SHORT_REQUEST_DURATION;

@Slf4j
@RequiredArgsConstructor
public class LagreTpForholdCommand implements Callable<Flux<PensjonforvalterResponse>> {

    private static final String PENSJON_TP_FORHOLD_URL = "/api/v1/tp/forhold";

    private final WebClient webClient;

    private final String token;

    private final PensjonTpForholdRequest lagreTpForholdRequest;

    @Override
    public Flux<PensjonforvalterResponse> call() {

        var callId = generateCallId();
        log.info("Pensjon lagre TP-forhold {}, callId: {}", lagreTpForholdRequest, callId);
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PENSJON_TP_FORHOLD_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(lagreTpForholdRequest)
                .retrieve()
                .bodyToFlux(PensjonforvalterResponse.class)
                .timeout(Duration.ofSeconds(SHORT_REQUEST_DURATION))
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> {
                    var description = WebClientError.describe(throwable);
                    return Mono.just(PensjonforvalterResponse
                            .builder()
                            .status(lagreTpForholdRequest
                                    .getMiljoer()
                                    .stream()
                                    .map(miljoe -> PensjonforvalterResponse.ResponseEnvironment
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
                                                    .path(PENSJON_TP_FORHOLD_URL)
                                                    .build())
                                            .build())
                                    .toList())
                            .build());
                });

    }

}
