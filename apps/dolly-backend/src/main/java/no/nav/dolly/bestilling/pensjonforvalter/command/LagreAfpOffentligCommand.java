package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AfpOffentligRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class LagreAfpOffentligCommand implements Callable<Mono<PensjonforvalterResponse>> {
    private static final String PEN_AFP_OFFENTLIG_URL = "/{miljoe}/api/mock-oppsett/{ident}";

    private final WebClient webClient;
    private final AfpOffentligRequest afpOffentligRequest;
    private final String miljoe;
    private final String token;

    public Mono<PensjonforvalterResponse> call() {

        var ident = afpOffentligRequest
                .getMocksvar().stream()
                .map(AfpOffentligRequest.AfpOffentligStub::getFnr)
                .findFirst().orElse(null);

        var callId = generateCallId();
        log.info("Pensjon afp-offentlig {} {}, callId: {}", miljoe, afpOffentligRequest, callId);
       
        return webClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(PEN_AFP_OFFENTLIG_URL)
                        .build(miljoe, ident))
                .header(AUTHORIZATION, "Bearer " + token)
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(afpOffentligRequest)
                .retrieve()
                .toBodilessEntity()
                .map(response -> pensjonforvalterResponse(miljoe, ident, HttpStatus.valueOf(response.getStatusCode().value())))
                .doOnError(WebClientFilter::logErrorMessage)
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

    private static PensjonforvalterResponse pensjonforvalterResponseFromError(String miljoe, String ident, Throwable error) {

        var miljoeResponse = PensjonforvalterResponse.ResponseEnvironment.builder()
                .miljo(miljoe)
                .response(PensjonforvalterResponse.Response.builder()
                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                .status(WebClientFilter.getStatus(error).value())
                                .reasonPhrase(WebClientFilter.getStatus(error).getReasonPhrase())
                                .build())
                        .message(WebClientFilter.getMessage(error))
                        .path(PEN_AFP_OFFENTLIG_URL.replace("{miljoe}", miljoe).replace("{ident}", ident))
                        .build())
                .build();

        return PensjonforvalterResponse.builder()
                .status(Collections.singletonList(miljoeResponse))
                .build();
    }
}