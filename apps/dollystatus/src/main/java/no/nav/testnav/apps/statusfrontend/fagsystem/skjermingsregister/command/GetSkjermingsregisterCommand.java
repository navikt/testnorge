package no.nav.testnav.apps.statusfrontend.fagsystem.skjermingsregister.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.skjermingsregister.SkjermingsregisterRequest;
import no.nav.testnav.apps.statusfrontend.fagsystem.skjermingsregister.SkjermingsregisterResourceStatus;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetSkjermingsregisterCommand
        implements Callable<Mono<SkjermingsregisterResourceStatus>> {

    private final WebClient webClient;
    private final String token;
    private final SkjermingsregisterRequest expectedRequest;
    private final LocalDateTime referenceTime;
    private final Duration timeout;

    @Override
    public Mono<SkjermingsregisterResourceStatus> call() {
        return webClient.get()
                .uri("/skjermingsregister/api/v1/skjerming/dolly")
                .headers(headers -> headers.setBearerAuth(token))
                .header("personident", expectedRequest.personident())
                .exchangeToMono(response -> {
                    if (response.statusCode() == HttpStatus.NOT_FOUND) {
                        return response.releaseBody().thenReturn(
                                SkjermingsregisterResourceStatus.emptyStatus());
                    }
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(JsonNode.class)
                                .map(body -> SkjermingsregisterResourceStatus.from(
                                        body,
                                        expectedRequest,
                                        referenceTime))
                                .switchIfEmpty(Mono.error(new IllegalStateException(
                                        "Skjermingsregister-oppslaget returnerte tom respons.")));
                    }
                    return response.createException()
                            .flatMap(exception ->
                                    Mono.<SkjermingsregisterResourceStatus>error(exception));
                })
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException());
    }
}
