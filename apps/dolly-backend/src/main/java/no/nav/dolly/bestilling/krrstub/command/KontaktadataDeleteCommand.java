package no.nav.dolly.bestilling.krrstub.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.krrstub.dto.DigitalKontaktdataResponse;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
@Slf4j
public class KontaktadataDeleteCommand implements Callable<Mono<DigitalKontaktdataResponse>> {

    private static final String DIGITAL_KONTAKT_URL = "/api/v2/person/kontaktinformasjon";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Timed(name = "providers", tags = {"operation", "krrstub_deleteKontaktdata"})
    public Mono<DigitalKontaktdataResponse> call() {

        var body = new HashMap<>();
        body.put("personidentifikator", ident);

        return webClient
                .method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder
                        .path(DIGITAL_KONTAKT_URL)
                        .build())
                .bodyValue(body)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .toBodilessEntity()
                .map(response -> DigitalKontaktdataResponse.builder()
                        .status(HttpStatus.valueOf(response.getStatusCode().value()))
                        .build())
                .doOnError(
                        throwable -> !(throwable instanceof WebClientResponseException.NotFound),
                        throwable -> WebClientError.log(throwable, log))
                .onErrorResume(error -> Mono.just(DigitalKontaktdataResponse.builder()
                        .status(WebClientFilter.getStatus(error))
                        .melding(WebClientFilter.getMessage(error))
                        .build()))
                .retryWhen(WebClientError.is5xxException());
    }

}
