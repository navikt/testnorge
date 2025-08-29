package no.nav.dolly.bestilling.udistub.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.udistub.domain.UdiPerson;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
@Slf4j
public class UdistubPutCommand implements Callable<Mono<UdiPersonResponse>> {

    private static final String UDISTUB_PERSON = "/api/v1/person";

    private final WebClient webClient;
    private final UdiPerson udiPerson;
    private final String token;

    @Override
    public Mono<UdiPersonResponse> call() {
        return webClient
                .put()
                .uri(uriBuilder -> uriBuilder.path(UDISTUB_PERSON).build())
                .headers(WebClientHeader.bearer(token))
                .headers(WebClientHeader.jwt(getUserJwt()))
                .body(BodyInserters.fromPublisher(Mono.just(udiPerson), UdiPerson.class))
                .retrieve()
                .toEntity(UdiPersonResponse.class)
                .map(response -> UdiPersonResponse.builder()
                        .person(Optional.ofNullable(response.getBody()).map(UdiPersonResponse::getPerson).orElse(null))
                        .status(HttpStatus.valueOf(response.getStatusCode().value()))
                        .type(UdiPersonResponse.InnsendingType.UPDATE)
                        .build())
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(throwable -> Mono.just(UdiPersonResponse.builder()
                        .person(udiPerson)
                        .status(throwable instanceof WebClientResponseException webClientResponseException ?
                                HttpStatus.valueOf(webClientResponseException.getStatusCode().value()) : HttpStatus.INTERNAL_SERVER_ERROR)
                        .reason(WebClientError.describe(throwable).getMessage())
                        .type(UdiPersonResponse.InnsendingType.UPDATE)
                        .build()))
                .retryWhen(WebClientError.is5xxException());
    }

}
