package no.nav.organisasjonforvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GenererNavnCommand implements Callable<Flux<NavnDTO>> {

    private final WebClient webClient;
    private final Integer antall;
    private final String accessToken;

    @Override
    public Flux<NavnDTO> call() {
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/navn")
                        .queryParam("antall", antall)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToFlux(NavnDTO.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(throwable -> Mono.empty());
    }
}