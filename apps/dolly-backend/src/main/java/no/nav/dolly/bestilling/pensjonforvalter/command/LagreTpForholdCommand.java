package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpForholdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
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
import static no.nav.dolly.util.TokenXUtil.getUserJwt;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class LagreTpForholdCommand implements Callable<Flux<PensjonforvalterResponse>> {

    private static final String PENSJON_TP_FORHOLD_URL = "/api/v1/tp/forhold";

    private final WebClient webClient;

    private final String token;

    private final PensjonTpForholdRequest lagreTpForholdRequest;

    public Flux<PensjonforvalterResponse> call() {

        var callId = generateCallId();
        log.info("Pensjon lagre TP-forhold {}, callId: {}", lagreTpForholdRequest, callId);
        
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PENSJON_TP_FORHOLD_URL)
                        .build())
                .httpRequest(httpRequest -> {
                    HttpClientRequest reactorRequest = httpRequest.getNativeRequest();
                    reactorRequest.responseTimeout(Duration.ofSeconds(REQUEST_DURATION));
                })
                .header(AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(lagreTpForholdRequest)
                .retrieve()
                .bodyToFlux(PensjonforvalterResponse.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(error ->
                        Mono.just(PensjonforvalterResponse.builder()
                                .status(lagreTpForholdRequest.getMiljoer().stream()
                                        .map(miljoe -> PensjonforvalterResponse.ResponseEnvironment.builder()
                                                .miljo(miljoe)
                                                .response(PensjonforvalterResponse.Response.builder()
                                                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                                .status(WebClientFilter.getStatus(error).value())
                                                                .reasonPhrase(WebClientFilter.getStatus(error).getReasonPhrase())
                                                                .build())
                                                        .message(WebClientFilter.getMessage(error))
                                                        .path(PENSJON_TP_FORHOLD_URL)
                                                        .build())
                                                .build())
                                        .toList())
                                .build()));
    }
}
