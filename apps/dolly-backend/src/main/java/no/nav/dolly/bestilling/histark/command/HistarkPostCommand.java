package no.nav.dolly.bestilling.histark.command;

import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.histark.domain.HistarkRequest;
import no.nav.dolly.bestilling.histark.domain.HistarkResponse;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.Callable;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class HistarkPostCommand implements Callable<Flux<HistarkResponse>> {

    private final WebClient webClient;
    private final HistarkRequest histarkRequest;
    private final String token;


    @Override
    public Flux<HistarkResponse> call() {

        return Flux.fromIterable(histarkRequest.getHistarkDokumenter()).flatMap(histarkDokument -> {
            var bodyBuilder = new MultipartBodyBuilder();
            bodyBuilder.part("file", histarkDokument.getFile().getBytes(StandardCharsets.UTF_8));
            bodyBuilder.part("metadata", histarkDokument.getMetadata().toString());
            return webClient.post()
                    .uri(builder ->
                            builder.path("/api/saksmapper/import").build())
                    .header(AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                    .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                    .retrieve()
                    .bodyToMono(Object.class)
                    .doOnNext(response -> log.info("Histark response: {}", response))
                    .map(response -> {
                        log.info("Histark response: {}", response);
                        return HistarkResponse.builder()
                                .histarkId(Json.pretty(response))
                                .build();
                    })
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .doOnError(WebClientFilter::logErrorMessage)
                    .onErrorResume(error -> Mono.just(HistarkResponse.builder()
                            .feilmelding(WebClientFilter.getMessage(error))
                            .build()));
        });
    }
}