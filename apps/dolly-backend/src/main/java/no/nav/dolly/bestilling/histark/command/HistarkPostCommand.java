package no.nav.dolly.bestilling.histark.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.histark.domain.HistarkRequest;
import no.nav.dolly.bestilling.histark.domain.HistarkResponse;
import no.nav.dolly.util.WebClientFilter;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
public class HistarkPostCommand implements Callable<Flux<HistarkResponse>> {

    private final WebClient webClient;
    private final HistarkRequest histarkRequest;
    private final String token;


    @Override
    public Flux<HistarkResponse> call() {

        return Flux.fromIterable(histarkRequest.getHistarkDokumenter()).flatMap(histarkDokument -> webClient.post()
                .uri(builder ->
                        builder.path("/api/saksmapper/import").build())
                .header(AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(histarkDokument)
                .retrieve()
                .bodyToMono(HistarkResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Mono.just(HistarkResponse.builder()
                        .feilmelding(WebClientFilter.getMessage(error))
                        .build())));
    }
}