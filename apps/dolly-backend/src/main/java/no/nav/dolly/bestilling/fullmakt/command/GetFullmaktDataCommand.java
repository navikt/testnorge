package no.nav.dolly.bestilling.fullmakt.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.fullmakt.dto.FullmaktPostResponse;
import no.nav.dolly.util.RequestHeaderUtil;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
@Slf4j
public class GetFullmaktDataCommand implements Callable<Flux<FullmaktPostResponse.Fullmakt>> {

    private static final String HENT_FULLMAKT_URL = "/api/fullmaktsgiver";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    public Flux<FullmaktPostResponse.Fullmakt> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(HENT_FULLMAKT_URL)
                        .build())
                .header(HEADER_NAV_CALL_ID, RequestHeaderUtil.getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header("fnr", ident)
                .headers(WebClientHeader.bearer(token))
                .headers(WebClientHeader.jwt(getUserJwt()))
                .retrieve()
                .bodyToFlux(FullmaktPostResponse.Fullmakt.class)
                .doOnError(
                        throwable -> !(throwable instanceof WebClientResponseException.NotFound),
                        WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(WebClientResponseException.NotFound.class::isInstance, throwable -> Flux.empty());
    }

}