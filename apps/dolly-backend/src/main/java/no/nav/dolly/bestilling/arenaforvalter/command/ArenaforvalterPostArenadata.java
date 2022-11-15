package no.nav.dolly.bestilling.arenaforvalter.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukereResponse;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class ArenaforvalterPostArenadata implements Callable<Flux<ArenaNyeBrukereResponse>> {

    private static final String ARENAFORVALTER_BRUKER = "/api/v1/bruker";

    private final WebClient webClient;
    private final ArenaNyeBrukere arenaNyeBrukere;
    private final String token;

    @Override
    public Flux<ArenaNyeBrukereResponse> call() {

        return webClient.post().uri(
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
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error ->
                        Flux.just(ArenaNyeBrukereResponse.builder()
                                .nyBrukerFeilList(List.of(ArenaNyeBrukereResponse.NyBrukerFeilV1.builder()
                                        .miljoe(arenaNyeBrukere.getNyeBrukere().get(0).getMiljoe())
                                        .personident(arenaNyeBrukere.getNyeBrukere().get(0).getPersonident())
                                        .melding(WebClientFilter.getMessage(error))
                                        .build()))
                                .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
