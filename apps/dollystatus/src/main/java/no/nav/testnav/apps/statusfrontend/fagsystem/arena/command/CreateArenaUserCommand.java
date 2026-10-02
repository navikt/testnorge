package no.nav.testnav.apps.statusfrontend.fagsystem.arena.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.arena.ArenaRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class CreateArenaUserCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final String token;
    private final ArenaRequest request;
    private final String callId;
    private final Duration timeout;

    @Override
    public Mono<Void> call() {
        return webClient.post()
                .uri("/arena/api/v1/bruker")
                .headers(headers -> headers.setBearerAuth(token))
                .header("Nav-Call-Id", callId)
                .header("Nav-Consumer-Id", "Dolly")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .filter(this::isSuccessful)
                .switchIfEmpty(Mono.error(new IllegalStateException(
                        "Arena-opprettingen returnerte ugyldig respons.")))
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException())
                .then();
    }

    private boolean isSuccessful(JsonNode response) {
        var errors = response.path("nyBrukerFeilList");
        if (errors.isArray() && !errors.isEmpty()) {
            return false;
        }
        var users = response.path("arbeidsokerList");
        if (!users.isArray() || users.isEmpty()) {
            return false;
        }
        for (var user : users) {
            if ("OK".equals(user.path("status").asString())) {
                return true;
            }
        }
        return false;
    }
}
