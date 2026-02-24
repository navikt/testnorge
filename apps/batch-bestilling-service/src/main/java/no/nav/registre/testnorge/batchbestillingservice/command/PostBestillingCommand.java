package no.nav.registre.testnorge.batchbestillingservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.batchbestillingservice.request.RsDollyBestillingRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class PostBestillingCommand implements Callable<Disposable> {

    private final WebClient webClient;
    private final String token;
    private final Long gruppeId;
    private final RsDollyBestillingRequest request;

    @Override
    public Disposable call() {
        log.info("Sender batch bestilling til Dolly backend for gruppe {}.", gruppeId);
        return webClient.post()
                .uri(builder -> builder
                        .path("/api/v1/gruppe/{gruppeId}/bestilling").build(gruppeId))
                .headers(WebClientHeader.bearer(token))
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .retryWhen(WebClientError.is5xxException())
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> {
                            log.warn("Fant ikke gruppe {}.", gruppeId);
                            return Mono.empty();
                        })
                .subscribe();
    }

}
