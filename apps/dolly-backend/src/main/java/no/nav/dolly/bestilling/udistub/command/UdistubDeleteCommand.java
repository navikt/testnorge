package no.nav.dolly.bestilling.udistub.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.util.CallIdUtil;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_PERSON_IDENT;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class UdistubDeleteCommand implements Callable<Flux<Void>> {

    private static final String UDISTUB_PERSON = "/api/v1/person";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    public Flux<Void> call() {

        return webClient
                .delete()
                .uri(uriBuilder -> uriBuilder.path(UDISTUB_PERSON).build())
                .header(HEADER_NAV_CALL_ID, CallIdUtil.generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToFlux(Void.class)
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Flux.empty())
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}