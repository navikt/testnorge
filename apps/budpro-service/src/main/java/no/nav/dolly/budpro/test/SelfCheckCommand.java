package no.nav.dolly.budpro.test;

import lombok.extern.slf4j.Slf4j;
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
                //.headers(WebClientHeaders.bearer(token))
                .retrieve()
                .bodyToMono(DummyDTO.class)
                .doOnError(e -> log.error("Failed to call self", e));
    }

}
