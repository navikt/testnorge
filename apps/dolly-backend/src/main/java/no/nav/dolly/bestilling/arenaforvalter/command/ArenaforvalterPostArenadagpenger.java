package no.nav.dolly.bestilling.arenaforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaDagpenger;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeDagpengerResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
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
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(arenaDagpenger)
                .retrieve()
                .bodyToFlux(ArenaNyeDagpengerResponse.class)
                .map(response -> {
                    response.setStatus(HttpStatus.OK);
                    response.setMiljoe(arenaDagpenger.getMiljoe());
                    return response;
                })
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .onErrorResume(throwable ->
                        Flux.just(ArenaNyeDagpengerResponse.builder()
                                .status(WebClientError.describe(throwable).getStatus())
                                .feilmelding(WebClientError.describe(throwable).getMessage())
                                .miljoe(arenaDagpenger.getMiljoe())
                                .build()))
                .retryWhen(WebClientError.is5xxException());
    }

}
