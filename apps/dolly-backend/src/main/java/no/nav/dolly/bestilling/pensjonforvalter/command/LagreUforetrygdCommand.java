package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonUforetrygdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.util.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class LagreUforetrygdCommand implements Callable<Flux<PensjonforvalterResponse>> {

    private static final String PENSJON_UT_URL = "/api/v1/vedtak/ut";

    private final WebClient webClient;

    private final String token;

    private final PensjonUforetrygdRequest uforetrygdRequest;

    public Flux<PensjonforvalterResponse> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PENSJON_UT_URL)
                        .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(uforetrygdRequest)
                .retrieve()
                .bodyToFlux(PensjonforvalterResponse.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(error ->
                        Mono.just(PensjonforvalterResponse.builder()
                                .status(uforetrygdRequest.getMiljoer().stream()
                                        .map(miljoe -> PensjonforvalterResponse.ResponseEnvironment.builder()
                                                .miljo(miljoe)
                                                .response(PensjonforvalterResponse.Response.builder()
                                                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                                .status(WebClientFilter.getStatus(error).value())
                                                                .reasonPhrase(WebClientFilter.getStatus(error).getReasonPhrase())
                                                                .build())
                                                        .message(WebClientFilter.getMessage(error))
                                                        .timestamp(LocalDateTime.now())
                                                        .path(PENSJON_UT_URL)
                                                        .build())
                                                .build())
                                        .toList())
                                .build()));
    }
}