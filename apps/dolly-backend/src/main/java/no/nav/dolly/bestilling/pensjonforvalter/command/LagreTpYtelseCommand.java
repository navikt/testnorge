package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreTpYtelseRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class LagreTpYtelseCommand implements Callable<Mono<PensjonforvalterResponse>> {
    private static final String PENSJON_TP_YTELSE_URL = "/api/v1/tp/ytelse";

    private final WebClient webClient;

    private final String token;

    private final LagreTpYtelseRequest lagreTpYtelseRequest;

    public Mono<PensjonforvalterResponse> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PENSJON_TP_YTELSE_URL)
                        .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(lagreTpYtelseRequest)
                .retrieve()
                .bodyToMono(PensjonforvalterResponse.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(error ->
                        Mono.just(PensjonforvalterResponse.builder()
                                .status(lagreTpYtelseRequest.getMiljoer().stream()
                                        .map(miljoe -> PensjonforvalterResponse.ResponseEnvironment.builder()
                                                .miljo(miljoe)
                                                .response(PensjonforvalterResponse.Response.builder()
                                                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                                                .reasonPhrase(WebClientFilter.getMessage(error))
                                                                .status(500)
                                                                .build())
                                                        .timestamp(LocalDateTime.now())
                                                        .path("/inntekt")
                                                        .build())
                                                .build())
                                        .toList())
                                .build()));
    }
}
