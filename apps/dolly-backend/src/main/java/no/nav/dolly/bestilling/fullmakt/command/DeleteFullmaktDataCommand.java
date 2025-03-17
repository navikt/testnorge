package no.nav.dolly.bestilling.fullmakt.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.util.RequestHeaderUtil;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@RequiredArgsConstructor
public class DeleteFullmaktDataCommand implements Callable<Mono<HttpStatusCode>> {

    private static final String DELETE_FULLMAKT_URL = "/api/fullmakt/{fullmaktId}";

    private final WebClient webClient;
    private final String ident;
    private final Integer fullmaktId;
    private final String token;

    public Mono<HttpStatusCode> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(DELETE_FULLMAKT_URL)
                        .build(fullmaktId))
                .header(HEADER_NAV_CALL_ID, RequestHeaderUtil.getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header("fnr", ident)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .toBodilessEntity()
                .map(ResponseEntity::getStatusCode)
                .doOnError(WebClientError.logTo(log))
                .doOnSuccess(response -> log.info("Fullmakt with id {} deleted for person with ident {}", fullmaktId, ident))
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log));
    }

}
