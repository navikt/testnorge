package no.nav.dolly.bestilling.arenaforvalter.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaArbeidssokerBruker;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class ArenaForvalterGetBrukerCommand implements Callable<Flux<ArenaArbeidssokerBruker>> {

    private static final String ARENAFORVALTER_BRUKER = "/api/v1/bruker";
    private final WebClient webClient;
    private final String ident;

    private final String miljoe;
    private final String token;

    @Override
    public Flux<ArenaArbeidssokerBruker> call() {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(ARENAFORVALTER_BRUKER)
                        .queryParam("filter-personident", ident)
                        .queryParam("filter-miljoe", miljoe)
                        .build())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToFlux(ArenaArbeidssokerBruker.class)
                .map(response -> {
                    response.setStatus(HttpStatus.OK);
                    return response;
                })
                .doOnError(WebClientFilter::logErrorMessage)
                .doOnError(throwable -> ArenaArbeidssokerBruker.builder()
                        .status(WebClientFilter.getStatus(throwable))
                        .feilmelding(WebClientFilter.getMessage(throwable))
                        .build())
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
