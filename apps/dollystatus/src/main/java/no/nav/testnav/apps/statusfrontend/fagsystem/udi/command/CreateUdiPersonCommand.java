package no.nav.testnav.apps.statusfrontend.fagsystem.udi.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.udi.UdiRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class CreateUdiPersonCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final String token;
    private final UdiRequest request;
    private final Duration timeout;

    @Override
    public Mono<Void> call() {
        return webClient.post()
                .uri("/udistub/api/v1/person")
                .headers(headers -> headers.setBearerAuth(token))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .filter(this::hasCreatedPerson)
                .switchIfEmpty(Mono.error(new IllegalStateException(
                        "UDI-opprettingen returnerte ugyldig respons.")))
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException())
                .then();
    }

    private boolean hasCreatedPerson(JsonNode response) {
        return request.ident().equals(response.path("person").path("ident").asString());
    }
}
