package no.nav.dolly.bestilling.histark.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.histark.domain.HistarkRequest;
import no.nav.dolly.bestilling.histark.domain.HistarkResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static org.springframework.web.reactive.function.BodyInserters.fromMultipartData;

@Slf4j
@RequiredArgsConstructor
public class HistarkPostCommand implements Callable<Mono<HistarkResponse>> {

    private final WebClient webClient;
    private final HistarkRequest histarkRequest;
    private final String token;

    @Override
    public Mono<HistarkResponse> call() {

        var body = new LinkedMultiValueMap<String, Object>();
        body.add("file", histarkRequest.getFile());
        body.add("metadata", histarkRequest.getMetadata().toString());

        return webClient
                .post()
                .uri(builder ->
                        builder.path("/histark/api/saksmapper/import") // requestParam metadata er overflødig
                                .build())
                .headers(WebClientHeader.bearer(token))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(fromMultipartData(body))
                .retrieve()
                .bodyToMono(HistarkResponse.class)
                .map(response -> {
                    response.setDokument(histarkRequest.getMetadata().getFilnavn());
                    return response;
                })
                .doOnNext(response -> log.info("Responsebody fra histark: {}", response))

                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> HistarkResponse.of(WebClientError.describe(throwable)));

    }

}
