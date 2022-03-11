package no.nav.dolly.web.consumers.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
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
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}

