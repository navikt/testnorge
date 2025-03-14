package no.nav.dolly.bestilling.arenaforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.arenaforvalter.dto.Aap115Request;
import no.nav.dolly.bestilling.arenaforvalter.dto.Aap115Response;
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
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(aap115Request)
                .retrieve()
                .bodyToFlux(Aap115Response.class)
                .map(response -> {
                    response.setStatus(HttpStatus.OK);
                    response.setMiljoe(aap115Request.getMiljoe());
                    return response;
                })
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .onErrorResume(error ->
                        Flux.just(Aap115Response.builder()
                                .status(WebClientFilter.getStatus(error))
                                .feilmelding(WebClientFilter.getMessage(error))
                                .miljoe(aap115Request.getMiljoe())
                                .build()))
                .retryWhen(WebClientError.is5xxException());
    }

}
