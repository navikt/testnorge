package no.nav.dolly.bestilling.arenaforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;

@RequiredArgsConstructor
@Slf4j
public class ArenaForvalterGetMiljoeCommand implements Callable<Flux<String>> {

    private static final String ARENAFORVALTER_ENVIRONMENTS = "/api/v1/miljoe";

    private final WebClient webClient;
    private final String token;

    @Override
    public Flux<String> call() {
        return webClient
                .get()
                .uri(
                        uriBuilder -> uriBuilder
                                .path(ARENAFORVALTER_ENVIRONMENTS)
                                .build())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(String[].class)
                .doOnError(WebClientError.logTo(log))
                .flatMapIterable(miljoer -> Arrays.stream(miljoer).toList())
                .retryWhen(WebClientError.is5xxException());
    }

}
