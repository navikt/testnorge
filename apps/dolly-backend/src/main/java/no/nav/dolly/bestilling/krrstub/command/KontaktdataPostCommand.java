package no.nav.dolly.bestilling.krrstub.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.krrstub.dto.DigitalKontaktdataResponse;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;

@RequiredArgsConstructor
@Slf4j
public class KontaktdataPostCommand implements Callable<Mono<DigitalKontaktdataResponse>> {

    private static final String DIGITAL_KONTAKT_URL = "/api/v2/kontaktinformasjon";

    private final WebClient webClient;
    private final DigitalKontaktdata digitalKontaktdata;
    private final String token;

    @Override
    public Mono<DigitalKontaktdataResponse> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(DIGITAL_KONTAKT_URL)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .headers(WebClientHeader.bearer(token))
                .bodyValue(digitalKontaktdata)
                .retrieve()
                .toBodilessEntity()
                .map(response -> DigitalKontaktdataResponse.builder()
                        .status(HttpStatus.valueOf(response.getStatusCode().value()))
                        .build())
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> DigitalKontaktdataResponse.of(WebClientError.describe(throwable)));
    }
}