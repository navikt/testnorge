package no.nav.dolly.bestilling.brregstub.domain.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_PERSON_IDENT;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@RequiredArgsConstructor
public class BrregDeleteCommand implements Callable<Flux<String>> {

    private static final String ROLLEOVERSIKT_URL = "/api/v2/rolleoversikt";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    public Flux<String> call() {

        return
            webClient.delete().uri(uriBuilder -> uriBuilder.path(ROLLEOVERSIKT_URL).build())
                    .header(HEADER_NAV_PERSON_IDENT, ident)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                    .retrieve()
                    .bodyToFlux(String.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .doOnError(throwable -> log.error(WebClientFilter.getMessage(throwable)));
    }
}
