package no.nav.dolly.proxy.auth;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public class FakedingsCommand implements Callable<Mono<String>> {

    private final WebClient webClient;
    private final String ident;

    @Override
    public Mono<String> call() {
        return webClient
                .get()
                .uri(uri -> uri
                        .path("/fake/tokenx")
                        .queryParam("pid", ident)
                        .queryParam("acr", "Level4")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(throwable -> log.error("Failed to get fakedings token", throwable));
    }

}
