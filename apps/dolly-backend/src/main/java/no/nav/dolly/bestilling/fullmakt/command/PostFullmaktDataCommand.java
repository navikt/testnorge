package no.nav.dolly.bestilling.fullmakt.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.fullmakt.dto.FullmaktResponse;
import no.nav.dolly.domain.resultset.fullmakt.RsFullmakt;
import no.nav.dolly.util.RequestHeaderUtil;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;
import static org.apache.http.util.TextUtils.isBlank;

@Slf4j
@RequiredArgsConstructor
public class PostFullmaktDataCommand implements Callable<Mono<FullmaktResponse>> {

    private static final String POST_FULLMAKT_URL = "/api/fullmakt";

    private final WebClient webClient;
    private final String token;
    private final String ident;
    private final RsFullmakt request;

    public Mono<FullmaktResponse> call() {

        if (isBlank(request.getFullmektig())) {
            log.error("Klarte ikke å hente fullmektig relasjon for ident: {} fra PDL forvalter ", ident);
            return Mono.just(FullmaktResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .melding("Fullmakt mangler fullmektig for ident: " + ident)
                    .build());
        }

        return webClient
                .post()
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
                .doOnError(WebClientError.logTo(log))
                .doOnError(throwable -> {
                    if (throwable instanceof WebClientResponseException ex && ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        log.error("Bad request mot repr-fullmakt, response: {}", ex.getResponseBodyAsString());
                    }
                })
                .doOnSuccess(response -> log.info("Fullmakt opprettet for person {}, response: {}", ident, response))
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log));

    }

}
