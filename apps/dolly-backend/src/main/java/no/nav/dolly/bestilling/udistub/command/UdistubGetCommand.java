package no.nav.dolly.bestilling.udistub.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonResponse;
import no.nav.dolly.util.RequestHeaderUtil;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
public class UdistubGetCommand implements Callable<Mono<UdiPersonResponse>> {

    private static final String UDISTUB_PERSON = "/api/v1/person";
    private static final String CONSUMER = "Dolly";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Mono<UdiPersonResponse> call() {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(UDISTUB_PERSON)
                        .pathSegment(ident).build())
                .header(HEADER_NAV_CALL_ID, RequestHeaderUtil.getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .toEntity(UdiPersonResponse.class)
                .map(response -> UdiPersonResponse.builder()
                        .person(response.hasBody() ? response.getBody().getPerson() : null)
                        .status(response.getStatusCode())
                        .build())
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(throwable -> Mono.just(UdiPersonResponse.builder()
                        .status(throwable instanceof WebClientResponseException webClientResponseException ?
                                webClientResponseException.getStatusCode() : HttpStatus.INTERNAL_SERVER_ERROR)
                        .reason(WebClientFilter.getMessage(throwable))
                        .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
