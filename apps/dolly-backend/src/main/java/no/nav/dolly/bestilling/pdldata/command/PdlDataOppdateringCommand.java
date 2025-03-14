package no.nav.dolly.bestilling.pdldata.command;

import io.netty.handler.timeout.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pdldata.dto.PdlResponse;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonUpdateRequestDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClientRequest;

import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.RequestTimeout.REQUEST_DURATION;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
@Slf4j
public class PdlDataOppdateringCommand implements Callable<Flux<PdlResponse>> {

    private static final String PDL_FORVALTER_PERSONER_URL = "/api/v1/personer/{ident}";

    private final WebClient webClient;
    private final String ident;
    private final PersonUpdateRequestDTO body;
    private final String token;

    public Flux<PdlResponse> call() {
        return webClient
                .put()
                .uri(PDL_FORVALTER_PERSONER_URL, ident)
                .httpRequest(httpRequest -> {
                    HttpClientRequest reactorRequest = httpRequest.getNativeRequest();
                    reactorRequest.responseTimeout(Duration.ofSeconds(REQUEST_DURATION));
                })
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToFlux(String.class)
                .map(resultat -> PdlResponse.builder()
                        .ident(resultat)
                        .status(HttpStatus.OK)
                        .build())
                .onErrorMap(TimeoutException.class, e -> new HttpTimeoutException("Timeout on PUT of ident %s".formatted(ident)))
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .onErrorResume(throwable -> Flux.just(PdlResponse.builder()
                        .status(WebClientFilter.getStatus(throwable))
                        .feilmelding(WebClientFilter.getMessage(throwable))
                        .build()))
                .retryWhen(WebClientError.is5xxException());
    }

}
