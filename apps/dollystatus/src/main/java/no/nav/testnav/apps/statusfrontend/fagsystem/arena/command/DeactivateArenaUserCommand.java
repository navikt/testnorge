package no.nav.testnav.apps.statusfrontend.fagsystem.arena.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class DeactivateArenaUserCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final String token;
    private final String ident;
    private final String environment;
    private final String callId;
    private final Duration timeout;

    @Override
    public Mono<Void> call() {
        return webClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/arena/api/v1/bruker")
                        .queryParam("miljoe", environment)
                        .queryParam("personident", ident)
                        .build())
                .headers(headers -> headers.setBearerAuth(token))
                .header("Nav-Call-Id", callId)
                .header("Nav-Consumer-Id", "Dolly")
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()
                            || response.statusCode() == HttpStatus.NOT_FOUND) {
                        return response.releaseBody();
                    }
                    return response.createException()
                            .flatMap(exception -> Mono.<Void>error(exception));
                })
                .timeout(timeout);
    }
}
