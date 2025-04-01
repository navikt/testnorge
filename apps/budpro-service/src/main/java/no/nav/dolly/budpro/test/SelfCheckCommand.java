package no.nav.dolly.budpro.test;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
class SelfCheckCommand implements Callable<Mono<DummyDTO>> {

    private final WebClient webClient;

    public SelfCheckCommand(WebClient webClient) {
        this.webClient = webClient
                .mutate()
                .baseUrl("http://localhost:8080")
                .build();
    }

    @Override
    public Mono<DummyDTO> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .host("localhost")
                        .port(8080)
                        .path("/failure/get")
                        .queryParam("httpStatus", "402")
                        .build())
                //.headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(DummyDTO.class)
                .retryWhen(WebClientError.is(HttpClientErrorException.class::isInstance))
                .doOnError(WebClientError.logTo(log));
    }

}
