package no.nav.dolly.consumer.kodeverk.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class KodeverkGetCommand implements Callable<Mono<KodeverkDTO>> {

    private static final String KODEVERK_URL = "/api/v1/kodeverk";
    private static final String KODEVERK = "kodeverk";

    private final WebClient webClient;
    private final String kodeverk;
    private final String token;

    @Override
    public Mono<KodeverkDTO> call() {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(KODEVERK_URL)
                        .queryParam(KODEVERK, kodeverk)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(KodeverkDTO.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Mono.just(KodeverkDTO.builder()
                        .build()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
