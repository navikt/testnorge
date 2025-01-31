package no.nav.dolly.bestilling.histark.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.histark.domain.HistarkRequest;
import no.nav.dolly.bestilling.histark.domain.HistarkResponse;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.Callable;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;

@Slf4j
@RequiredArgsConstructor
public class HistarkPostCommand implements Callable<Mono<HistarkResponse>> {

    private final WebClient webClient;
    private final HistarkRequest histarkRequest;
    private final String token;

    @Override
    public Mono<HistarkResponse> call() {

                    log.info("Sender metadata: {}", histarkRequest.getMetadata().toString());
                    return webClient.post()
                            .uri(builder ->
                                    builder.path("/api/saksmapper/import")
                                            .queryParam("metadata", histarkRequest.getMetadata().toString())
                                            .build())
                            .header(AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .body(fromFormData("metadata", histarkRequest.getMetadata().toString())
                                    .with("file", Arrays.toString(histarkRequest.getFile().getBytes(StandardCharsets.UTF_8))))
                            .exchangeToMono(clientResponse -> {
                                log.info("Status fra histark: {}", clientResponse.statusCode());
                                log.info("Responseheaders fra histark: {}", clientResponse.headers().asHttpHeaders());
                                if (clientResponse.statusCode().isError()) {
                                    return Mono.just(HistarkResponse.builder()
                                            .feilmelding("Feil ved opprettelse av saksmappe, status: " + clientResponse.statusCode())
                                            .build());
                                }
                                return clientResponse.bodyToMono(HistarkResponse.class);
                            })
                            .doOnNext(response -> log.info("Responsebody fra histark: {}", response))
                            .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                                    .filter(WebClientFilter::is5xxException))
                            .doOnError(WebClientFilter::logErrorMessage)
                            .onErrorResume(throwable -> Mono.empty());
    }
}
