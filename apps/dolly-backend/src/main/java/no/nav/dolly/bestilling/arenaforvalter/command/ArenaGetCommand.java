package no.nav.dolly.bestilling.arenaforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.arenaforvalter.dto.ArenaStatusResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class ArenaGetCommand implements Callable<Mono<ArenaStatusResponse>> {

    private static final String ARENA_URL = "/arena/{miljoe}/arena/syntetiser/brukeroppfolging/personstatusytelse";
    private static final String FODSELSNR = "fodselsnr";

    private final WebClient webClient;
    private final String ident;
    private final String miljoe;
    private final String token;

    @Override
    public Mono<ArenaStatusResponse> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ARENA_URL)
                        .build(miljoe))
                .headers(WebClientHeader.bearer(token))
                .header(FODSELSNR, ident)
                .retrieve()
                .onStatus(HttpStatus.NO_CONTENT::equals, ClientResponse::createException)
                .bodyToMono(ArenaStatusResponse.class)
                .map(status -> {
                    status.setStatus(HttpStatus.OK);
                    status.setMiljoe(miljoe);
                    return status;
                })
                .doOnError(throwable -> !(throwable instanceof WebClientResponseException exception && exception.getStatusCode().value() == 204),
                        WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> ArenaStatusResponse.of(WebClientError.describe(throwable), miljoe));
    }
}