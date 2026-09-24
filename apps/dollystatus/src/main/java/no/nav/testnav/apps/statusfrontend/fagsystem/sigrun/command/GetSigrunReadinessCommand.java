package no.nav.testnav.apps.statusfrontend.fagsystem.sigrun.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetSigrunReadinessCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final String token;
    private final Duration timeout;

    @Override
    public Mono<Void> call() {
        return webClient.get()
                .uri("/sigrunstub/internal/health/readiness")
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .toBodilessEntity()
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException())
                .then();
    }
}
