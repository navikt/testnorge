package no.nav.dolly.bestilling.arenaforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukereResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
@Slf4j
public class ArenaforvalterPostArenaBruker implements Callable<Flux<ArenaNyeBrukereResponse>> {

    private static final String ARENAFORVALTER_BRUKER = "/api/v1/bruker";

    private final WebClient webClient;
    private final ArenaNyeBrukere arenaNyeBrukere;
    private final String token;

    @Override
    public Flux<ArenaNyeBrukereResponse> call() {
        return webClient
                .post()
                .uri(
                        uriBuilder -> uriBuilder
                                .path(ARENAFORVALTER_BRUKER)
                                .build())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .headers(WebClientHeader.bearer(token))
                .headers(WebClientHeader.jwt(getUserJwt()))
                .bodyValue(arenaNyeBrukere)
                .retrieve()
                .bodyToFlux(ArenaNyeBrukereResponse.class)
                .map(response -> {
                    response.setStatus(HttpStatus.OK);
                    response.setMiljoe(arenaNyeBrukere.getNyeBrukere().getFirst().getMiljoe());
                    return response;
                })
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(throwable -> ArenaNyeBrukereResponse.of(WebClientError.describe(throwable), arenaNyeBrukere.getNyeBrukere().getFirst().getMiljoe()))
                .retryWhen(WebClientError.is5xxException());
    }

}
