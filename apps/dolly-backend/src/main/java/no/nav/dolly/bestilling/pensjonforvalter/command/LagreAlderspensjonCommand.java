package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonVedtakRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClientRequest;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.RequestTimeout.REQUEST_DURATION;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class LagreAlderspensjonCommand implements Callable<Flux<PensjonforvalterResponse>> {

    private static final String PENSJON_AP_VEDTAK_URL = "/api/v4/vedtak/ap";
    private static final String PENSJON_AP_SOKNAD_URL = "/api/v4/vedtak/ap/soknad";

    private final WebClient webClient;

    private final String token;

    private final AlderspensjonRequest alderspensjonRequest;

    @Override
    public Flux<PensjonforvalterResponse> call() {
        var callId = generateCallId();
        log.info("Pensjon lagre alderspensjon {}, callId: {}", alderspensjonRequest, callId);
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(alderspensjonRequest instanceof AlderspensjonVedtakRequest ?
                                PENSJON_AP_VEDTAK_URL :
                                PENSJON_AP_SOKNAD_URL)
                        .build())
                .httpRequest(httpRequest -> {
                    HttpClientRequest reactorRequest = httpRequest.getNativeRequest();
                    reactorRequest.responseTimeout(Duration.ofSeconds(REQUEST_DURATION));
                })
                .header(AUTHORIZATION, "Bearer " + token)
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(alderspensjonRequest)
                .retrieve()
                .bodyToFlux(PensjonforvalterResponse.class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable ->
                        Mono.just(PensjonforvalterResponse
                                .builder()
                                .status(alderspensjonRequest
                                        .getMiljoer()
                                        .stream()
                                        .map(miljoe -> {
                                            var description = WebClientError.describe(throwable);
                                            return PensjonforvalterResponse.ResponseEnvironment
                                                    .builder()
                                                    .miljo(miljoe)
                                                    .response(PensjonforvalterResponse.Response
                                                            .builder()
                                                            .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                                    .status(description.getStatus().value())
                                                                    .reasonPhrase(description.getStatus().getReasonPhrase())
                                                                    .build())
                                                            .message(description.getMessage())
                                                            .path(alderspensjonRequest instanceof AlderspensjonVedtakRequest ?
                                                                    PENSJON_AP_VEDTAK_URL : PENSJON_AP_SOKNAD_URL)
                                                            .build())
                                                    .build();
                                        })
                                        .toList())
                                .build()));
    }

}
