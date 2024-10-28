package no.nav.dolly.bestilling.yrkesskade.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.yrkesskade.dto.SaksoversiktDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class YrkesskadeGetCommand implements Callable<Mono<SaksoversiktDTO>> {

    private static final String YRKESSKADE_URL = "/api/v1/yrkesskader/{ident}";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Mono<SaksoversiktDTO> call() {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(YRKESSKADE_URL)
                        .build(ident))
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("ident", ident)
                .retrieve()
                .bodyToMono(SaksoversiktDTO.class)
                .map(resultat -> SaksoversiktDTO.builder()
                        .status(HttpStatusCode.valueOf(200))
                        .saker(resultat.getSaker())
                        .build())
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(throwable -> Mono.just(SaksoversiktDTO.builder()
                                .status(WebClientFilter.getStatus(throwable))
                                .melding(WebClientFilter.getMessage(throwable))
                                .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}