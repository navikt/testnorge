package no.nav.dolly.bestilling.instdata.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.instdata.domain.InstdataKdiDTO;
import no.nav.dolly.bestilling.instdata.domain.InstdataResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class InstdataKdiPostCommand implements Callable<Mono<InstdataResponse>> {

    private static final String INSTDATA_URL = "/inst/api/v2/kdi/person";

    private final WebClient webClient;
    private final String ident;
    private final InstdataKdiDTO instdata;
    private final String token;

    @Override
    public Mono<InstdataResponse> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(INSTDATA_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(instdata)
                .retrieve()
                .toBodilessEntity()
                .map(resultat -> InstdataResponse.builder()
                        .personident(ident)
                        .status(HttpStatus.valueOf(resultat.getStatusCode().value()))
                        .environments(List.of(instdata.getEnvironment()))
                        .build())
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> InstdataResponse.of(WebClientError.describe(throwable), ident, List.of(instdata.getEnvironment())));
    }
}
