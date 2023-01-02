package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreInntektRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.util.WebClientFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class LagrePoppInntektCommand implements Callable<Flux<PensjonforvalterResponse>> {
    private static final String MILJO_HEADER = "environment";
    private static final String POPP_INNTEKT_URL = "/api/v1/inntekt";

    private final WebClient webClient;
    private final String token;
    private final LagreInntektRequest lagreInntektRequest;
    private final String miljoe;

    public Flux<PensjonforvalterResponse> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(POPP_INNTEKT_URL)
                        .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(MILJO_HEADER, StringUtils.upperCase(miljoe))
                .bodyValue(lagreInntektRequest)
                .retrieve()
                .bodyToFlux(String.class)
                .map(response -> pensjonforvalterResponse(miljoe, response))
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(error -> Mono.just(pensjonforvalterResponseFromError(miljoe, error)));
    }

    private static PensjonforvalterResponse pensjonforvalterResponse(String miljoe, String response) {

        var miljoeResponse = PensjonforvalterResponse.ResponseEnvironment.builder()
                .miljo(miljoe)
                .response(PensjonforvalterResponse.Response.builder()
                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                .reasonPhrase(response)
                                .status(200)
                                .build())
                        .timestamp(LocalDateTime.now())
                        .path(POPP_INNTEKT_URL)
                        .build())
                .build();

        return PensjonforvalterResponse.builder()
                .status(Arrays.asList(miljoeResponse))
                .build();
    }

    private static PensjonforvalterResponse pensjonforvalterResponseFromError(String miljoe, Throwable error) {
        var miljoeResponse = PensjonforvalterResponse.ResponseEnvironment.builder()
                .miljo(miljoe)
                .response(PensjonforvalterResponse.Response.builder()
                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                .reasonPhrase(WebClientFilter.getMessage(error))
                                .status(500)
                                .build())
                        .timestamp(LocalDateTime.now())
                        .path(POPP_INNTEKT_URL)
                        .build())
                .build();

        return PensjonforvalterResponse.builder()
                .status(Arrays.asList(miljoeResponse))
                .build();
    }
}
