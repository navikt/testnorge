package no.nav.dolly.bestilling.arbeidssoekerregisteret.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class SlettArbeidssoekerregisteretCommand implements Callable<Mono<HttpStatus>> {

    private final WebClient webClient;
    private final String ident;
    private final String token;

    public Mono<HttpStatus> call() {
        return webClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/arbeidssoekerregistrering/{identitetsnummer}")
                        .build(ident))
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .toBodilessEntity()
                .map(response -> HttpStatus.valueOf(response.getStatusCode().value()))
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException());
    }

}
