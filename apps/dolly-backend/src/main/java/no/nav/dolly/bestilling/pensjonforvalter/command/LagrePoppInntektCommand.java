package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonPoppInntektRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClientRequest;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.RequestTimeout.REQUEST_DURATION;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class LagrePoppInntektCommand implements Callable<Flux<PensjonforvalterResponse>> {

    private static final String POPP_INNTEKT_URL = "/api/v1/inntekt";

    private final WebClient webClient;
    private final String token;
    private final PensjonPoppInntektRequest pensjonPoppInntektRequest;

    public Flux<PensjonforvalterResponse> call() {

        var callId = generateCallId();
        log.info("Popp lagre inntekt {}, callId: {}", pensjonPoppInntektRequest, callId);
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(POPP_INNTEKT_URL)
                        .build())
                .httpRequest(httpRequest -> {
                    HttpClientRequest reactorRequest = httpRequest.getNativeRequest();
                    reactorRequest.responseTimeout(Duration.ofSeconds(REQUEST_DURATION));
                })
                .header(AUTHORIZATION, "Bearer " + token)
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(pensjonPoppInntektRequest)
                .retrieve()
                .bodyToFlux(PensjonforvalterResponse.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(error ->
                        Mono.just(PensjonforvalterResponse.builder()
                                .status(pensjonPoppInntektRequest.getMiljoer().stream()
                                        .map(miljoe -> PensjonforvalterResponse.ResponseEnvironment.builder()
                                                .miljo(miljoe)
                                                .response(PensjonforvalterResponse.Response.builder()
                                                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                                .status(WebClientFilter.getStatus(error).value())
                                                                .reasonPhrase(WebClientFilter.getStatus(error).getReasonPhrase())
                                                                .build())
                                                        .message(WebClientFilter.getMessage(error))
                                                        .path(POPP_INNTEKT_URL)
                                                        .build())
                                                .build())
                                        .toList())
                                .build()));
    }
}