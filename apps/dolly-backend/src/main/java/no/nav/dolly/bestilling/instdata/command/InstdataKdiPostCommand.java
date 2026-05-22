package no.nav.dolly.bestilling.instdata.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.instdata.domain.InstdataKdiDTO;
import no.nav.dolly.bestilling.instdata.domain.InstdataKdiResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class InstdataKdiPostCommand implements Callable<Mono<InstdataKdiResponse>> {

    private static final String INSTDATA_URL = "/inst/api/v2/kdi/person";

    private final WebClient webClient;
    private final String ident;
    private final InstdataKdiDTO instdata;
    private final String token;

    @Override
    public Mono<InstdataKdiResponse> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(INSTDATA_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(instdata)
                .retrieve()
                .toBodilessEntity()
                .map(resultat -> InstdataKdiResponse.builder()
                        .status(HttpStatus.valueOf(resultat.getStatusCode().value()))
                        .ident(ident)
                        .environment(instdata.getEnvironment())
                        .build())
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> InstdataKdiResponse.of(WebClientError.describe(throwable), ident, instdata.getEnvironment()));
    }
}