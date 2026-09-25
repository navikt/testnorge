package no.nav.testnav.apps.statusfrontend.fagsystem.skjermingsregister.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.skjermingsregister.SkjermingsregisterRequest;
import no.nav.testnav.apps.statusfrontend.fagsystem.skjermingsregister.SkjermingsregisterResourceStatus;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class CreateSkjermingsregisterCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final String token;
    private final SkjermingsregisterRequest request;
    private final Duration timeout;

    @Override
    public Mono<Void> call() {
        return webClient.post()
                .uri("/skjermingsregister/api/v1/skjerming/dolly")
                .headers(headers -> headers.setBearerAuth(token))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(body -> SkjermingsregisterResourceStatus.from(
                        body,
                        request,
                        request.skjermetFra()))
                .filter(SkjermingsregisterResourceStatus::expectedDataPresent)
                .switchIfEmpty(Mono.error(new IllegalStateException(
                        "Skjermingsregister-opprettingen returnerte ugyldig respons.")))
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException())
                .then();
    }
}
