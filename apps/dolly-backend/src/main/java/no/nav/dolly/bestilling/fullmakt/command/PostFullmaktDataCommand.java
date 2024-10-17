package no.nav.dolly.bestilling.fullmakt.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.fullmakt.dto.FullmaktResponse;
import no.nav.dolly.domain.resultset.fullmakt.RsFullmakt;
import no.nav.dolly.util.RequestHeaderUtil;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@RequiredArgsConstructor
public class PostFullmaktDataCommand implements Callable<Mono<FullmaktResponse>> {

    private static final String POST_FULLMAKT_URL = "/api/fullmakt";

    private final WebClient webClient;
    private final String token;
    private final String ident;
    private final RsFullmakt request;

    public Mono<FullmaktResponse> call() {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(POST_FULLMAKT_URL)
                        .build())
                .body(BodyInserters.fromValue(request))
                .header(HEADER_NAV_CALL_ID, RequestHeaderUtil.getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header("fnr", ident)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToMono(FullmaktResponse.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .doOnError(throwable -> {
                    if (throwable instanceof WebClientResponseException ex) {
                        if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                            log.error("Bad request mot pdl-fullmakt, response: {}", ex.getResponseBodyAsString());
                        }
                    }
                })
                .doOnSuccess(response -> log.info("Fullmakt opprettet for person {}, response: {}", ident, response))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(WebClientFilter::logErrorMessage);
    }
}
