package no.nav.testnav.apps.statusfrontend.fagsystem.pdl.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetPdlForvalterPersonCommand implements Callable<Mono<Boolean>> {

    private final WebClient webClient;
    private final String token;
    private final String ident;
    private final Duration timeout;

    @Override
    public Mono<Boolean> call() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/personer")
                        .queryParam("identer", ident)
                        .queryParam("sidenummer", 0)
                        .queryParam("pagesize", 10)
                        .build())
                .headers(headers -> headers.setBearerAuth(token))
                .exchangeToMono(response -> {
                    if (response.statusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.just(false);
                    }
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(JsonNode.class)
                                .map(this::containsPerson);
                    }
                    return response.createException()
                            .flatMap(exception -> Mono.<Boolean>error(exception));
                })
                .timeout(timeout);
    }

    private boolean containsPerson(JsonNode response) {
        if (!response.isArray()) {
            return false;
        }
        for (var person : response) {
            if (ident.equals(person.path("person").path("ident").asString())) {
                return true;
            }
        }
        return false;
    }
}
