package no.nav.dolly.bestilling.arenaforvalter.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.arenaforvalter.dto.ArenaResponse;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class ArenaGetCommand implements Callable<Mono<ArenaResponse>> {

    private static final String ARENA_URL = "{miljoe}/arena/syntetiser/brukeroppfolging/personstatusytelse";
    private static final String IDENT = "fodselsnr";

    private final WebClient webClient;
    private final String ident;
    private final String miljoe;
    private final String token;

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
                .bodyToMono(ArenaResponse.class)
                .map(response -> {
                    response.setStatus(HttpStatus.OK);
                    response.setMiljoe(miljoe);
                    return response;
                })
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error ->
                        Mono.just(ArenaResponse.builder()
                                .status(WebClientFilter.getStatus(error))
                                .feilmelding(WebClientFilter.getMessage(error))
                                .miljoe(miljoe)
                                .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
