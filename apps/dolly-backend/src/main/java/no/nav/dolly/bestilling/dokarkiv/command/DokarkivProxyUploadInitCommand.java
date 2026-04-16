package no.nav.dolly.bestilling.dokarkiv.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class DokarkivProxyUploadInitCommand implements Callable<Mono<String>> {

    private final WebClient webClient;
    private final String token;

    @Override
    @SuppressWarnings("unchecked")
    public Mono<String> call() {

        return webClient
                .post()
                .uri("/dokarkiv/upload/init")
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("uploadId"))
                .doOnNext(uploadId -> log.info("Dokarkiv proxy upload init: {}", uploadId))
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException());
    }

}
