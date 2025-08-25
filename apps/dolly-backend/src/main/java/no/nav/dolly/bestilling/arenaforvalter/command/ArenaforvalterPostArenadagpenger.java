package no.nav.dolly.bestilling.arenaforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaDagpenger;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeDagpengerResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;

@RequiredArgsConstructor
@Slf4j
public class ArenaforvalterPostArenadagpenger implements Callable<Flux<ArenaNyeDagpengerResponse>> {

    private static final String ARENAFORVALTER_DAGPENGER = "/api/v1/dagpenger";

    private final WebClient webClient;
    private final ArenaDagpenger arenaDagpenger;
    private final String token;

    @Override
    public Flux<ArenaNyeDagpengerResponse> call() {
        return webClient
                .post()
                .uri(
                        uriBuilder -> uriBuilder
                                .path(ARENAFORVALTER_DAGPENGER)
                                .build())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .headers(WebClientHeader.bearer(token))
                .bodyValue(arenaDagpenger)
                .retrieve()
                .bodyToFlux(ArenaNyeDagpengerResponse.class)
                .map(response -> {
                    response.setStatus(HttpStatus.OK);
                    response.setMiljoe(arenaDagpenger.getMiljoe());
                    return response;
                })
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(throwable -> ArenaNyeDagpengerResponse.of(WebClientError.describe(throwable), arenaDagpenger.getMiljoe()))
                .retryWhen(WebClientError.is5xxException());
    }

}
