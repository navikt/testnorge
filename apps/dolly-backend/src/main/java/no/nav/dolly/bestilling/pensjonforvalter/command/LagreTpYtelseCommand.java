package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpYtelseRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClientRequest;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.RequestTimeout.REQUEST_DURATION;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class LagreTpYtelseCommand implements Callable<Flux<PensjonforvalterResponse>> {
    private static final String PENSJON_TP_YTELSE_URL = "/api/v1/tp/ytelse";

    private final WebClient webClient;

    private final String token;

    private final PensjonTpYtelseRequest pensjonTpYtelseRequest;

    @Override
    public Flux<PensjonforvalterResponse> call() {
        var callId = generateCallId();
        log.info("Pensjon lagre TP-ytelse {}, callId: {}", pensjonTpYtelseRequest, callId);
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PENSJON_TP_YTELSE_URL)
                        .build())
                .httpRequest(httpRequest -> {
                    HttpClientRequest reactorRequest = httpRequest.getNativeRequest();
                    reactorRequest.responseTimeout(Duration.ofSeconds(REQUEST_DURATION));
                })
                .header(AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(pensjonTpYtelseRequest)
                .retrieve()
                .bodyToFlux(PensjonforvalterResponse.class)
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(error ->
                        Mono.just(PensjonforvalterResponse.builder()
                                .status(pensjonTpYtelseRequest.getMiljoer().stream()
                                        .map(miljoe -> PensjonforvalterResponse.ResponseEnvironment.builder()
                                                .miljo(miljoe)
                                                .response(PensjonforvalterResponse.Response.builder()
                                                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                                .status(WebClientFilter.getStatus(error).value())
                                                                .reasonPhrase(WebClientFilter.getStatus(error).getReasonPhrase())
                                                                .build())
                                                        .message(WebClientFilter.getMessage(error))
                                                        .path(PENSJON_TP_YTELSE_URL)
                                                        .build())
                                                .build())
                                        .toList())
                                .build()));
    }

}