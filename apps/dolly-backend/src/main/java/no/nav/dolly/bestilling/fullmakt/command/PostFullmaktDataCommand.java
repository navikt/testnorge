package no.nav.dolly.bestilling.fullmakt.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.fullmakt.dto.FullmaktPostResponse;
import no.nav.dolly.domain.resultset.fullmakt.RsFullmakt;
import no.nav.dolly.util.RequestHeaderUtil;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static org.apache.http.util.TextUtils.isBlank;

@Slf4j
@RequiredArgsConstructor
public class PostFullmaktDataCommand implements Callable<Mono<FullmaktPostResponse>> {

    private static final String POST_FULLMAKT_URL = "/api/fullmakt";

    private final WebClient webClient;
    private final String token;
    private final String ident;
    private final RsFullmakt request;

    public Mono<FullmaktPostResponse> call() {

        if (isBlank(request.getFullmektig())) {
            log.error("Klarte ikke å hente fullmektig relasjon for ident: {} fra PDL forvalter ", ident);
            return Mono.just(FullmaktPostResponse.builder()
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
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(FullmaktPostResponse.class)
                .doOnError(throwable -> {
                    if (throwable instanceof WebClientResponseException webClienResponseException
                            && !webClienResponseException.getStatusCode().is4xxClientError()) {
                       WebClientError.logTo(log).accept(throwable);
                    }
                })
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> {
                    var description = WebClientError.describe(throwable);
                    return Mono.just(FullmaktPostResponse.builder()
                                .melding(description.getMessage())
                                .status(description.getStatus())
                        .build());
                });
    }
}
