package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonVedtakRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class LagreAlderspensjonCommand implements Callable<Flux<PensjonforvalterResponse>> {

    private static final String PENSJON_AP_VEDTAK_URL = "/api/v3/vedtak/ap";
    private static final String PENSJON_AP_SOKNAD_URL = PENSJON_AP_VEDTAK_URL + "/soknad";

    private final WebClient webClient;

    private final String token;

    private final AlderspensjonRequest alderspensjonRequest;

    public Flux<PensjonforvalterResponse> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(alderspensjonRequest instanceof AlderspensjonVedtakRequest ?
                                PENSJON_AP_VEDTAK_URL : PENSJON_AP_SOKNAD_URL)
                        .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(alderspensjonRequest)
                .retrieve()
                .bodyToFlux(PensjonforvalterResponse.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(error ->
                        Mono.just(PensjonforvalterResponse.builder()
                                .status(alderspensjonRequest.getMiljoer().stream()
                                        .map(miljoe -> PensjonforvalterResponse.ResponseEnvironment.builder()
                                                .miljo(miljoe)
                                                .response(PensjonforvalterResponse.Response.builder()
                                                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                                .status(WebClientFilter.getStatus(error).value())
                                                                .reasonPhrase(WebClientFilter.getStatus(error).getReasonPhrase())
                                                                .build())
                                                        .message(WebClientFilter.getMessage(error))
                                                        .path(alderspensjonRequest instanceof AlderspensjonVedtakRequest ?
                                                                PENSJON_AP_VEDTAK_URL : PENSJON_AP_SOKNAD_URL)
                                                        .build())
                                                .build())
                                        .toList())
                                .build()));
    }
}