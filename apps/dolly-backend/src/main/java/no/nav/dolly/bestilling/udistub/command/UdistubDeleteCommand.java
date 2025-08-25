package no.nav.dolly.bestilling.udistub.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_PERSON_IDENT;

@RequiredArgsConstructor
@Slf4j
public class UdistubDeleteCommand implements Callable<Mono<UdiPersonResponse>> {

    private static final String UDISTUB_PERSON = "/api/v1/person";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    public Mono<UdiPersonResponse> call() {
        return webClient
                .delete()
                .uri(uriBuilder -> uriBuilder.path(UDISTUB_PERSON).build())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .toBodilessEntity()
                .map(response -> UdiPersonResponse.builder()
                        .status(HttpStatus.valueOf(response.getStatusCode().value()))
                        .build())
                .doOnError(
                        throwable -> !(throwable instanceof WebClientResponseException.NotFound),
                        WebClientError.logTo(log))
                .onErrorResume(throwable -> Mono.empty())
                .retryWhen(WebClientError.is5xxException());
    }

}