package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.OpprettPersonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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
public class OpprettPersonCommand implements Callable<Mono<ResponseEntity<PensjonforvalterResponse>>> {

    private static final String PENSJON_OPPRETT_PERSON_URL = "/api/v1/person";

    private final WebClient webClient;

    private final String token;

    private final OpprettPersonRequest opprettPersonRequest;

    public Mono<ResponseEntity<PensjonforvalterResponse>> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PENSJON_OPPRETT_PERSON_URL)
                        .build())
                .header(AUTHORIZATION, token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(opprettPersonRequest)
                .retrieve()
                .toEntity(PensjonforvalterResponse.class)
                .doOnError(error -> log.error(WebClientFilter.getMessage(error), error))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty());
    }
}
