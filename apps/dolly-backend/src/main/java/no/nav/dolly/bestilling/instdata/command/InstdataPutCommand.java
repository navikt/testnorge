package no.nav.dolly.bestilling.instdata.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.instdata.domain.InstdataDTO;
import no.nav.dolly.bestilling.instdata.domain.InstdataResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class InstdataPutCommand implements Callable<Mono<InstdataResponse>> {

    private static final String INSTDATA_URL = "/inst/api/v1/institusjonsopphold/{id}";
    private static final String ENVIRONMENT = "environment";

    private final WebClient webClient;
    private final InstdataDTO instdata;
    private final String miljoe;
    private final String token;

    @Override
    public Mono<InstdataResponse> call() {

        log.info("Instdata endre til {}: {}", miljoe, instdata);

        return webClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(INSTDATA_URL)
                        .queryParam(ENVIRONMENT, miljoe)
                        .build(instdata.getOppholdId()))
                .headers(WebClientHeader.bearer(token))
                .bodyValue(instdata)
                .retrieve()
                .toBodilessEntity()
                .map(resultat -> InstdataResponse.builder()
                        .status(HttpStatus.valueOf(resultat.getStatusCode().value()))
                        .environments(List.of(miljoe))
                        .build())
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> {
                    var error = WebClientError.describe(throwable);
                    if (error.getMessage().contains("Institusjonsopphold med id %s finnes ikke"
                            .formatted(instdata.getOppholdId()))) {
                        return Mono.just(InstdataResponse.builder()
                                .status(HttpStatus.OK)
                                .environments(List.of(miljoe))
                                .build());
                    } else {
                    return Mono.just(InstdataResponse.builder()
                            .status(error.getStatus())
                            .feilmelding(error.getMessage())
                            .environments(List.of(miljoe))
                            .build());
                    }
                });
    }
}