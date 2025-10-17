package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AfpOffentligRequest;
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
public class LagreAfpOffentligCommand implements Callable<Mono<PensjonforvalterResponse>> {
    private static final String PEN_AFP_OFFENTLIG_URL = "/{miljoe}/api/mock-oppsett/{ident}";

    private final WebClient webClient;
    private final AfpOffentligRequest afpOffentligRequest;
    private final String ident;
    private final String miljoe;
    private final String token;

    public Mono<PensjonforvalterResponse> call() {

        var callId = generateCallId();
        log.info("Pensjon afp-offentlig {} {}, callId: {}", miljoe, afpOffentligRequest, callId);
       
        return webClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(PEN_AFP_OFFENTLIG_URL)
                        .build(miljoe, ident))
                .headers(WebClientHeader.bearer(token))
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(afpOffentligRequest)
                .retrieve()
                .toBodilessEntity()
                .map(response -> pensjonforvalterResponse(miljoe, ident, HttpStatus.valueOf(response.getStatusCode().value())))
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(error -> Mono.just(pensjonforvalterResponseFromError(miljoe, ident, error)));
    }

    private static PensjonforvalterResponse pensjonforvalterResponse(String miljoe, String ident, HttpStatus status) {

        var miljoeResponse = PensjonforvalterResponse.ResponseEnvironment.builder()
                .miljo(miljoe)
                .response(PensjonforvalterResponse.Response.builder()
                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                .status(status.value())
                                .reasonPhrase(status.getReasonPhrase())
                                .build())
                        .path(PEN_AFP_OFFENTLIG_URL.replace("{miljoe}", miljoe).replace("{ident}", ident))
                        .build())
                .build();

        return PensjonforvalterResponse.builder()
                .status(Collections.singletonList(miljoeResponse))
                .build();
    }

    private static PensjonforvalterResponse pensjonforvalterResponseFromError(String miljoe, String ident, Throwable throwable) {
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
                        .path(PEN_AFP_OFFENTLIG_URL.replace("{miljoe}", miljoe).replace("{ident}", ident))
                        .build())
                .build();
        return PensjonforvalterResponse
                .builder()
                .status(Collections.singletonList(miljoeResponse))
                .build();
    }
}