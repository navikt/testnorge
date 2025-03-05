package no.nav.dolly.budpro.test;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
class SelfCheckCommand implements Callable<Mono<DummyDTO>> {

    private final WebClient webClient;
    private final String token;

    public SelfCheckCommand(WebClient webClient, String token) {
        this.webClient = webClient
                .mutate()
                .baseUrl("http://localhost:8080")
                .build();
        this.token = token;
    }

    @Override
    public Mono<DummyDTO> call() {
        var webClientErrorHandler = new WebClientError.Handler();
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .host("localhost")
                        .port(8080)
                        .path("/failure/get")
                        .queryParam("httpStatus", "402")
                        .queryParam("delayInMillis", 100000)
                        .build())
                .headers(WebClientHeaders.bearer(token))
                .retrieve()
                .onStatus(HttpStatusCode::isError, webClientErrorHandler::log)
                .bodyToMono(DummyDTO.class)
                .retryWhen(WebClientError.is(HttpClientErrorException.class::isInstance, 1, 0))
                .doOnError(webClientErrorHandler::log);

    }

}
