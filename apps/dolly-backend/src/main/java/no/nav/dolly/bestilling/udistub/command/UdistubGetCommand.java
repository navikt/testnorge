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

import java.util.Optional;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class UdistubGetCommand implements Callable<Mono<UdiPersonResponse>> {

    private static final String UDISTUB_PERSON = "/api/v1/person";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Mono<UdiPersonResponse> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(UDISTUB_PERSON)
                        .pathSegment(ident).build())
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .toEntity(UdiPersonResponse.class)
                .map(response -> UdiPersonResponse
                        .builder()
                        .person(Optional
                                .ofNullable(response.getBody())
                                .map(UdiPersonResponse::getPerson)
                                .orElse(null))
                        .status(HttpStatus.valueOf(response.getStatusCode().value()))
                        .build())
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(throwable -> Mono.just(UdiPersonResponse.builder()
                        .status(throwable instanceof WebClientResponseException webClientResponseException ?
                                HttpStatus.valueOf(webClientResponseException.getStatusCode().value()) : HttpStatus.INTERNAL_SERVER_ERROR)
                        .reason(WebClientError.describe(throwable).getMessage())
                        .build()))
                .retryWhen(WebClientError.is5xxException());
    }

}
