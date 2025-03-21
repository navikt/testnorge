package no.nav.dolly.web.consumers.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetTokenCommand implements Callable<Mono<String>> {

    private final WebClient webClient;
    private final String token;
    private final String id;

    @Override
    public Mono<String> call() {
        return webClient
                .get()
                .uri(builder -> builder.path("/api/v1/brukere/{id}/token").build(id))
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(WebClientError.is5xxException());
    }

}

