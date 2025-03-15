package no.nav.dolly.bestilling.krrstub.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.krrstub.dto.DigitalKontaktdataResponse;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

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
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(digitalKontaktdata)
                .retrieve()
                .toBodilessEntity()
                .map(response -> DigitalKontaktdataResponse.builder()
                        .status(HttpStatus.valueOf(response.getStatusCode().value()))
                        .build())
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .onErrorResume(error -> Mono.just(DigitalKontaktdataResponse.builder()
                        .status(WebClientError.describe(error).getStatus())
                        .melding(WebClientError.describe(error).getMessage())
                        .build()))
                .retryWhen(WebClientError.is5xxException());
    }

}
