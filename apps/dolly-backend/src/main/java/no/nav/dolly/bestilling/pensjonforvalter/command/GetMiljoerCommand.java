package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@RequiredArgsConstructor
public class GetMiljoerCommand implements Callable<Mono<Set<String>>> {

    private static final String MILJOER_HENT_TILGJENGELIGE_URL = "/api/v1/miljo";

    private final WebClient webClient;
    private final String token;

    public Mono<Set<String>> call() {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(MILJOER_HENT_TILGJENGELIGE_URL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Set<String>>() {
                })
                .doOnError(error -> log.error(WebClientFilter.getMessage(error), error))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(throwable -> Mono.empty());
    }
}
