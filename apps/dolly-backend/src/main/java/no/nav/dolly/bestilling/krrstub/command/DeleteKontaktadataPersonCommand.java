package no.nav.dolly.bestilling.krrstub.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.util.RequestHeaderUtil;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@RequiredArgsConstructor
public class DeleteKontaktadataPersonCommand implements Callable<Flux<HttpStatus>> {

    private static final String DIGITAL_KONTAKT_URL = "/api/v2/person/kontaktinformasjon";

    private final WebClient webClient;

    private final String ident;
    private final String token;

    @Timed(name = "providers", tags = { "operation", "krrstub_deleteKontaktdataPerson" })
    public Flux<HttpStatus> call() {

        return webClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(DIGITAL_KONTAKT_URL)
                        .build())
                .header(HEADER_NAV_CALL_ID, RequestHeaderUtil.getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .retrieve()
                .toEntity(Void.class)
                .flatMapMany(response -> Flux.just(response.getStatusCode()))
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(
                        throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Flux.empty()
                )
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }

}
