package no.nav.dolly.bestilling.pdldata.command;

import io.netty.handler.timeout.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.net.http.HttpTimeoutException;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class PdlDataSlettCommand implements Callable<Flux<Void>> {

    private static final String PDL_FORVALTER_URL = "/api/v1/personer/{ident}";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    public Flux<Void> call() {
        return webClient
                .delete()
                .uri(PDL_FORVALTER_URL, ident)
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToFlux(Void.class)

                .onErrorMap(TimeoutException.class, e -> new HttpTimeoutException("Timeout on DELETE of ident %s".formatted(ident)))
                .doOnError(
                        throwable -> !(throwable instanceof WebClientResponseException.NotFound),
                        WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Flux.empty());
    }

}
