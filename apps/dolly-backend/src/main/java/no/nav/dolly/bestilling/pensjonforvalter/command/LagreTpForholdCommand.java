package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreTpForholdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class LagreTpForholdCommand implements Callable<Mono<ResponseEntity<PensjonforvalterResponse>>> {

    private static final String PENSJON_TP_FORHOLD_URL = "/api/v1/tp/forhold";

    private final WebClient webClient;

    private final String token;

    private final LagreTpForholdRequest lagreTpForholdRequest;

    public Mono<ResponseEntity<PensjonforvalterResponse>> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PENSJON_TP_FORHOLD_URL)
                        .build())
                .header(AUTHORIZATION, token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(lagreTpForholdRequest)
                .retrieve()
                .toEntity(PensjonforvalterResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
