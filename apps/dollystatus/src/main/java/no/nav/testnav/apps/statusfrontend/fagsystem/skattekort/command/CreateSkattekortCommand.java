package no.nav.testnav.apps.statusfrontend.fagsystem.skattekort.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.skattekort.SkattekortRequest;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class CreateSkattekortCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final String token;
    private final FunctionalTestEnvironment environment;
    private final SkattekortRequest request;
    private final Duration timeout;

    @Override
    public Mono<Void> call() {
        return webClient.post()
                .uri("/skattekort/{environment}/api/v1/person/opprett",
                        environment.name().toLowerCase())
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(token))
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException())
                .then();
    }
}
