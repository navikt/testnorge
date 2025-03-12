package no.nav.dolly.bestilling.arenaforvalter.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukereResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
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
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(arenaNyeBrukere)
                .retrieve()
                .bodyToFlux(ArenaNyeBrukereResponse.class)
                .map(response -> {
                    response.setStatus(HttpStatus.OK);
                    response.setMiljoe(arenaNyeBrukere.getNyeBrukere().get(0).getMiljoe());
                    return response;
                })
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error ->
                        Flux.just(ArenaNyeBrukereResponse.builder()
                                .status(WebClientFilter.getStatus(error))
                                .feilmelding(WebClientFilter.getMessage(error))
                                .miljoe(arenaNyeBrukere.getNyeBrukere().get(0).getMiljoe())
                                .build()))
                .retryWhen(WebClientError.is5xxException());
    }

}
