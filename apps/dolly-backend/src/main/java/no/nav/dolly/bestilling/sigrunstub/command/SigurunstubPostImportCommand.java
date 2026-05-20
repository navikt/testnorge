package no.nav.dolly.bestilling.sigrunstub.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubImportRequest;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubResponse;
import no.nav.dolly.util.RequestHeaderUtil;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@Slf4j
public class SigurunstubPostImportCommand implements Callable<Mono<SigrunstubResponse>> {

    private final WebClient webClient;
    private final String importUrl;
    private final String ident;
    private final String token;

    @Override
    public Mono<SigrunstubResponse> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(importUrl).build())
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HEADER_NAV_CALL_ID, RequestHeaderUtil.getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .headers(WebClientHeader.bearer(token))
                .body(BodyInserters.fromValue(SigrunstubImportRequest.builder()
                        .norskident(ident)
                        .build()))
                .retrieve()
                .toBodilessEntity()
                .map(response -> SigrunstubResponse.builder()
                        .ident(ident)
                        .status(HttpStatus.valueOf(response.getStatusCode().value()))
                        .build())
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(error -> SigrunstubResponse.of(WebClientError.describe(error), ident));
    }
}