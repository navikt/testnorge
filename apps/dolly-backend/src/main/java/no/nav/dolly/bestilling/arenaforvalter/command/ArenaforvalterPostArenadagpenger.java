package no.nav.dolly.bestilling.arenaforvalter.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaDagpenger;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeDagpengerResponse;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class ArenaforvalterPostArenadagpenger implements Callable<Flux<ArenaNyeDagpengerResponse>> {

    private static final String ARENAFORVALTER_DAGPENGER = "/api/v1/dagpenger";

    private final WebClient webClient;
    private final ArenaDagpenger arenaDagpenger;
    private final String token;

    @Override
    public Flux<ArenaNyeDagpengerResponse> call() {

        return webClient.post().uri(
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
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error ->
                        Flux.just(ArenaNyeDagpengerResponse.builder()
                                .nyeDagpFeilList(List.of(ArenaNyeDagpengerResponse.NyDagpFeilV1.builder()
                                        .miljoe(arenaDagpenger.getMiljoe())
                                        .personident(arenaDagpenger.getPersonident())
                                        .melding(WebClientFilter.getMessage(error))
                                        .build()))
                                .build()))
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.InternalServerError,
                        throwable -> Mono.empty());
    }
}
