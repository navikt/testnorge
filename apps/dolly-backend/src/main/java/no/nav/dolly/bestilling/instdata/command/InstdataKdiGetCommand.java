package no.nav.dolly.bestilling.instdata.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.instdata.domain.InstdataKdiDTO;
import no.nav.dolly.bestilling.instdata.domain.InstdataKdiResponse;
import no.nav.dolly.bestilling.instdata.domain.InstdataRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class InstdataKdiGetCommand implements Callable<Mono<InstdataKdiResponse>> {

    private static final String INSTDATA_URL = "/inst/api/v2/kdi/person/soek";

    private final WebClient webClient;
    private final String ident;
    private final String miljoe;
    private final String token;

    @Override
    public Mono<InstdataKdiResponse> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(INSTDATA_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(InstdataRequest.builder()
                        .norskident(ident)
                        .environment(miljoe)
                        .build())
                .retrieve()
                .bodyToMono(InstdataKdiDTO.class)
                .map(resultat -> InstdataKdiResponse.builder()
                        .status(HttpStatus.OK)
                        .ident(ident)
                        .environment(miljoe)
                        .instdataKdi(resultat)
                        .build())
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> InstdataKdiResponse.of(WebClientError.describe(throwable), ident, miljoe));
    }
}
