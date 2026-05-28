package no.nav.dolly.bestilling.instdata.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.instdata.domain.InstdataKdiResponse;
import no.nav.dolly.bestilling.instdata.domain.InstdataRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class InstdataKdiDeleteCommand implements Callable<Mono<InstdataKdiResponse>> {

    private static final String INSTDATA_KDI_URL = "/inst/api/v2/kdi/person";

    private final WebClient webClient;
    private final String ident;
    private final String miljoe;
    private final String token;

    @Override
    public Mono<InstdataKdiResponse> call() {

        return webClient
                .method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder
                        .path(INSTDATA_KDI_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(InstdataRequest.builder()
                        .norskident(ident)
                        .environment(miljoe)
                        .build())
                .retrieve()
                .toBodilessEntity()
                .map(resultat -> InstdataKdiResponse.builder()
                        .ident(ident)
                        .status(HttpStatus.valueOf(resultat.getStatusCode().value()))
                        .environment(miljoe)
                        .build())
                .doOnError(throwable ->
                                !(throwable instanceof WebClientResponseException.BadRequest),
                        WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> InstdataKdiResponse.of(WebClientError.describe(throwable), ident, miljoe));
    }
}
