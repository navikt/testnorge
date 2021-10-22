package no.nav.testnav.libs.reactivesessionsecurity.exchange.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
                .post()
                .uri(builder ->builder.path("/api/v1/brukere/{id}/token").build(id))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(String.class);
    }
}
