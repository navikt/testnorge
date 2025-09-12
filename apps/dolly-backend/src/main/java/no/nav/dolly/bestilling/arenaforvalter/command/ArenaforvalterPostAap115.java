package no.nav.dolly.bestilling.arenaforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.arenaforvalter.dto.Aap115Request;
import no.nav.dolly.bestilling.arenaforvalter.dto.Aap115Response;
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
public class ArenaforvalterPostAap115 implements Callable<Flux<Aap115Response>> {

    private static final String ARENAFORVALTER_AAP115 = "/api/v1/aap115";

    private final WebClient webClient;
    private final Aap115Request aap115Request;
    private final String token;

    @Override
    public Flux<Aap115Response> call() {
        return webClient
                .post()
                .uri(
                        uriBuilder -> uriBuilder
                                .path(ARENAFORVALTER_AAP115)
                                .build())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .headers(WebClientHeader.bearer(token))
                .bodyValue(aap115Request)
                .retrieve()
                .bodyToFlux(Aap115Response.class)
                .map(response -> {
                    response.setStatus(HttpStatus.OK);
                    response.setMiljoe(aap115Request.getMiljoe());
                    return response;
                })
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(throwable -> Aap115Response.of(WebClientError.describe(throwable), aap115Request.getMiljoe()));
    }
}