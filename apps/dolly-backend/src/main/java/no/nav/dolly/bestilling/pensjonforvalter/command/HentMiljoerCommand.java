package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
@Slf4j
public class HentMiljoerCommand implements Callable<Mono<Set<String>>> {

    private static final String MILJOER_HENT_TILGJENGELIGE_URL = "/api/v1/miljo";

    private final WebClient webClient;
    private final String token;

    @Override
    public Mono<Set<String>> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(MILJOER_HENT_TILGJENGELIGE_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .headers(WebClientHeader.jwt(getUserJwt()))
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Set<String>>() {
                })
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> Mono.just(Set.of("q1", "q2")))
                .cache(Duration.ofHours(1));
    }

}