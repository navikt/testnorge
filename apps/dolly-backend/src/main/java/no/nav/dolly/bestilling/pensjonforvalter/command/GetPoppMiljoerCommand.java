package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.util.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;

@Slf4j
@RequiredArgsConstructor
public class GetPoppMiljoerCommand implements Callable<Mono<Set<String>>> {

    private static final String MILJOER_HENT_TILGJENGELIGE_URL = "/environment";

    private final WebClient webClient;
    private final String token;

    public Mono<Set<String>> call() {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(MILJOER_HENT_TILGJENGELIGE_URL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve()
                .bodyToMono(String[].class)
                .map(resultat -> Stream.of(resultat)
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet()))
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(throwable -> Mono.just(emptySet()));
    }
}
