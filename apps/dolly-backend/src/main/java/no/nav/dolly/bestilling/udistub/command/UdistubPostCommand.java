package no.nav.dolly.bestilling.udistub.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.udistub.domain.UdiPerson;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class UdistubPostCommand implements Callable<Mono<UdiPersonResponse>> {

    private static final String UDISTUB_PERSON = "/api/v1/person";

    private final WebClient webClient;
    private final UdiPerson udiPerson;
    private final String token;

    @Override
    public Mono<UdiPersonResponse> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(UDISTUB_PERSON).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .body(BodyInserters.fromPublisher(Mono.just(udiPerson), UdiPerson.class))
                .retrieve()
                .toEntity(UdiPersonResponse.class)
                .map(response -> UdiPersonResponse.builder()
                        .person(response.hasBody() ? response.getBody().getPerson() : null)
                        .status(HttpStatus.valueOf(response.getStatusCode().value()))
                        .type(UdiPersonResponse.InnsendingType.NEW)
                        .build())
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(throwable -> Mono.just(UdiPersonResponse.builder()
                        .person(udiPerson)
                        .status(throwable instanceof WebClientResponseException webClientResponseException ?
                                HttpStatus.valueOf(webClientResponseException.getStatusCode().value()) : HttpStatus.INTERNAL_SERVER_ERROR)
                        .reason(WebClientFilter.getMessage(throwable))
                        .type(UdiPersonResponse.InnsendingType.NEW)
                        .build()))
                .retryWhen(WebClientError.is5xxException());
    }

}
