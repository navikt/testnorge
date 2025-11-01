package no.nav.dolly.bestilling.instdata.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.instdata.domain.DeleteResponse;
import no.nav.dolly.bestilling.instdata.domain.InstdataRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class InstdataDeleteCommand implements Callable<Mono<DeleteResponse>> {

    private static final String INSTDATA_URL = "/inst/api/v1/institusjonsopphold/person/slett";

    private final WebClient webClient;
    private final String ident;
    private final List<String> miljoer;
    private final String token;

    @Override
    public Mono<DeleteResponse> call() {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(INSTDATA_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(InstdataRequest.builder()
                        .personident(ident)
                        .environments(miljoer)
                        .build())
                .retrieve()
                .toBodilessEntity()
                .map(resultat -> DeleteResponse.builder()
                        .ident(ident)
                        .status(HttpStatus.valueOf(resultat.getStatusCode().value()))
                        .build())
                .doOnError(
                        throwable -> !(throwable instanceof WebClientResponseException.BadRequest),
                        WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> DeleteResponse.of(WebClientError.describe(throwable), ident));
    }
}
