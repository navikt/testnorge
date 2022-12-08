package no.nav.dolly.bestilling.udistub.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.udistub.domain.UdiPerson;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonResponse;
import no.nav.dolly.util.RequestHeaderUtil;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class UdistubPutCommand implements Callable<Mono<UdiPersonResponse>> {

    private static final String CONSUMER = "Dolly";
    private static final String UDISTUB_PERSON = "/api/v1/person";

    private final WebClient webClient;
    private final UdiPerson udiPerson;
    private final String token;

    @Override
    public Mono<UdiPersonResponse> call() {

        return webClient
                .put()
                .uri(uriBuilder -> uriBuilder.path(UDISTUB_PERSON).build())
                .header(HEADER_NAV_CALL_ID, RequestHeaderUtil.getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .body(BodyInserters.fromPublisher(Mono.just(udiPerson), UdiPerson.class))
                .retrieve()
                .bodyToMono(UdiPersonResponse.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(throwable -> Mono.just(UdiPersonResponse.builder()
                        .person(udiPerson)
                        .status(throwable instanceof WebClientResponseException webClientResponseException ?
                                webClientResponseException.getStatusCode() : HttpStatus.INTERNAL_SERVER_ERROR)
                        .reason(WebClientFilter.getMessage(throwable))
                        .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
