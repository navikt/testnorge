package no.nav.dolly.bestilling.dokarkiv.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
@Slf4j
public class DokarkivGetMiljoeCommand implements Callable<Mono<List<String>>> {

    private static final String DOKARKIV_PROXY_ENVIRONMENTS = "/rest/miljoe";

    private final WebClient webClient;
    private final String token;

    @Override
    public Mono<List<String>> call() {
        return webClient
                .get()
                .uri(
                        uriBuilder -> uriBuilder
                                .path(DOKARKIV_PROXY_ENVIRONMENTS)
                                .build())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .headers(WebClientHeader.bearer(token))
                .headers(WebClientHeader.jwt(getUserJwt()))
                .retrieve()
                .bodyToMono(String[].class)
                .map(Arrays::asList)
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(error -> Mono.just(List.of("q1", "q2", "q4")))
                .retryWhen(WebClientError.is5xxException());
    }

}
