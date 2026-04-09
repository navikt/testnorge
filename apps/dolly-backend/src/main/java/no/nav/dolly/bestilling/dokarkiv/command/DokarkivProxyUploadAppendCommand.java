package no.nav.dolly.bestilling.dokarkiv.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class DokarkivProxyUploadAppendCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final String uploadId;
    private final String chunk;
    private final String token;

    @Override
    public Mono<Void> call() {

        return webClient
                .post()
                .uri("/dokarkiv/upload/{uploadId}/append", uploadId)
                .headers(WebClientHeader.bearer(token))
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(chunk)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException());
    }

}
