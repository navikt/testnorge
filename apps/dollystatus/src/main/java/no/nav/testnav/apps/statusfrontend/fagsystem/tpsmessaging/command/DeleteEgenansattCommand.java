package no.nav.testnav.apps.statusfrontend.fagsystem.tpsmessaging.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class DeleteEgenansattCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final String token;
    private final String ident;
    private final List<String> environments;
    private final Duration timeout;

    @Override
    public Mono<Void> call() {
        return webClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/personer/{ident}/egenansatt")
                        .queryParam("miljoer", environments)
                        .build(ident))
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .flatMap(response ->
                        TpsMessagingCommandSupport.requireSuccessful(response, environments))
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException());
    }
}
