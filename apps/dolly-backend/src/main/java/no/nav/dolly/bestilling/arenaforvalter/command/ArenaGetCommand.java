package no.nav.dolly.bestilling.arenaforvalter.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.arenaforvalter.dto.ArenaResponse;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class ArenaGetCommand implements Callable<Mono<ArenaResponse>> {

    private static final String ARENA_URL = "{miljoe}/arena/syntetiser/brukeroppfolging/personstatusytelse";
    private static final String IDENT = "fodselsnr";

    private WebClient webClient;
    private String ident;
    private String miljoe;
    private String token;

    @Override
    public Mono<ArenaResponse> call() {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                                .path(ARENA_URL)
                                .build(miljoe))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(IDENT, ident)
                .retrieve()
                .bodyToFlux(ArenaResponse.class)
                .map(response -> {
                    response.setStatus(HttpStatus.OK);
                    response.setMiljoe(aapRequest.getMiljoe());
                    return response;
                })
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error ->
                        Flux.just(ArenaResponse.builder()
                                .status(WebClientFilter.getStatus(error))
                                .feilmelding(WebClientFilter.getMessage(error))
//                                .miljoe(aapRequest.getMiljoe())
                                .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
}
